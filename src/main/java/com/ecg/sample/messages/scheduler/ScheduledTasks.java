package com.ecg.sample.messages.scheduler;

import com.ecg.sample.messages.dto.ExternalServerResponseDto;
import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.service.ExternalServerService;
import com.ecg.sample.messages.singleton.MessageSingleton;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mahdi Sharifi
 * @since 10/3/22
 */

@Component
@Slf4j
public class ScheduledTasks {

    private final ExternalServerService externalService;
    private static final AtomicInteger counter= new AtomicInteger(0);
    private static final Map<Integer, List<Message>> partitionedMessagesReadyForSendMap = new WeakHashMap<>(); // defined as a field due to aggregate the messages.

    /**
     * For testing wee need this methods
     */
    public int  getPartitionedMessagesReadyForSendMapSize() {
        return partitionedMessagesReadyForSendMap.size();
    }
    public void clearPartitionedMessagesReadyForSendMap() {
        partitionedMessagesReadyForSendMap.clear();
    }

    public ScheduledTasks(ExternalServerService externalService) {
        this.externalService = externalService;
    }

    @Scheduled(cron = "${scheduler.cron}", zone = "${scheduler.zone}") //https://crontab.guru/
    public synchronized void partitionAndSendToExternalServer() throws InterruptedException {
        try {
            log.info("##Start scheduler->Partitioned map size: " + partitionedMessagesReadyForSendMap.size() + " ;Current list size: " + MessageSingleton.INSTANCE.getSize());
            int maxAttempts = 1; //If the external service goes down, we can send that part of the data that was not consumed by the consumer again.
            int pageSize = 2; // Size of each part of data for sending to external service.
            int sleepTime = 5000; // wait time between each retry attempt

            List<Message> list = MessageSingleton.INSTANCE.getMessages();
            partitionDataAnAddCopyToMap(list,pageSize);
            log.info("#Partitioned map new size: " + partitionedMessagesReadyForSendMap.size());

            MessageSingleton.INSTANCE.clearList();
            tryToSendToExternalServer(maxAttempts, pageSize, sleepTime);
            //In my scenario, I leave them to the current WeakHashMap not a Queue for simplicity
            log.info("#Partitioned map size lef for another round: " + partitionedMessagesReadyForSendMap.size());
        }catch (InterruptedException ex){
            log.error("#Exception occurred in scheduler. Message is: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     *  Partitioned data and add them into WeakHashMap.
     */
    public synchronized void partitionDataAnAddCopyToMap(List<Message> list,int pageSize) {
        List<List<Message>> partitionedData = Lists.partition(list, pageSize);
        for (List<Message> partitionedDatum : partitionedData) {
            //Add partitioned list to WeakHashMap as a temporary repository. Deleted object will be garbage collected in the next GC cycle.
            partitionedMessagesReadyForSendMap.put(counter.getAndIncrement(), new ArrayList<>(partitionedDatum));
        }
    }
    public void tryToSendToExternalServer(int maxAttempts, int pageSize, int sleepTime) throws InterruptedException {
        for (int i = 0; i < maxAttempts && !partitionedMessagesReadyForSendMap.isEmpty(); i++) {
            log.info("#Try to send: " + partitionedMessagesReadyForSendMap.size() + " parts ;Page size: " + pageSize + " ;Attempt " + (i + 1) + "/" + maxAttempts);
            sendToExternalServerAndRemoveSuccessValues(); // Try to send them to consumer
            if (!partitionedMessagesReadyForSendMap.isEmpty()) { // There are some unsuccessful send messages.
                log.info("#Wait for " + (sleepTime / 1000) + " seconds, and trying to send left parts again.");
                Thread.sleep(sleepTime);
            }
        }
    }

    private synchronized void sendToExternalServerAndRemoveSuccessValues() {
        List<Integer> markToRemove=new ArrayList<>();
        int size = partitionedMessagesReadyForSendMap.size();
        int part=1;
        for (Map.Entry<Integer, List<Message>> currentPart  :partitionedMessagesReadyForSendMap.entrySet()) {
            Optional<ExternalServerResponseDto> responseDtoOptional = externalService.sendMessageListToExternalServer(currentPart.getValue());
            if (responseDtoOptional.isPresent()) {
                markToRemove.add(currentPart.getKey());
                log.info("#Part " + part + "/" + size + " was sent successfully. " + responseDtoOptional.get());
            } else
                log.error("#Part " + part + "/" + size + " failed to send. ");
            part++;
        }
        //WeakHashmap is not synchronized, it is 'failed fast'. If used ConcurrentHash, It would not need this part
        for (Integer key: markToRemove) {
            partitionedMessagesReadyForSendMap.remove(key);
        }
    }
}