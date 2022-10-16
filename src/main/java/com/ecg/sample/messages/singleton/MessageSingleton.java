package com.ecg.sample.messages.singleton;

import com.ecg.sample.messages.dto.StatisticsDto;
import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.model.MessagesContainer;
import com.ecg.sample.messages.dto.OccurrenceDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Mahdi Sharifi
 * @since 10/7/22
 */

public enum MessageSingleton {

    INSTANCE;
    private static final MessagesContainer messagesContainer = new MessagesContainer();

    public List<Message> getMessages() {
        return messagesContainer.getMessages();
    }

    public int getSize() {
        return getMessages().size();
    }

    public void addMessage(Message message) {
        MessageSingleton.messagesContainer.addMessage(message);
    }

    public void clearList() {
        MessageSingleton.messagesContainer.clear();
    }

    /**
     * the number of messages posted in the last minute
     * the average (mean) length of unique words in all the messages received in last minute
     * the number of occurrences of each word contained in the messages posted in the last minute;
     * the word separator is any not alphanumeric character. For example,
     * if in the last minute the application received the two messages "test message1" and "test message2", you MUST count:
     * word	count
     * test	2
     * message1	1
     * message2	1
     * @return StatisticsDto
     */
    //You MAY optimize the procedure for time complexity or you MAY prefer code readibility.
    public StatisticsDto calculateStatistics() {
        //First of all,Filter the list and find last minutes message.
        List<Message> messageListOfLastMinute = getMessageListOfLastMinute();
        int messageListSize = messageListOfLastMinute.size();//Get the size of last minute messages.
        //Split messages by none alphanumeric characters and add them to a new list
        List<String> flatList = getListOfNoneAlphanumericMessages(messageListOfLastMinute);
        // Count occurrences of each string and create a map of them
        Map<String, Long> occurrencesMap = flatList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // Convert occurrence map to sorted occurrence list
        List<OccurrenceDto> occurrenceList = toOccurrenceSortedList(occurrencesMap);
        return getStatisticsDto(messageListSize,occurrencesMap, occurrenceList);
    }

    /**
     * Get occurrencesMap and occurrenceList then create StatisticsDto
     * @return StatisticsDto
     */
    private StatisticsDto getStatisticsDto(int messageListSize,Map<String, Long> occurrencesMap, List<OccurrenceDto> occurrenceList) {
        int postedDifferentMessageNo = occurrencesMap.size();
        long totalLengthOfDifferentMessages = occurrencesMap.keySet().stream().mapToInt(String::length).sum();

        Float averageLength=null;
        if (postedDifferentMessageNo != 0) {
            BigDecimal totalLengthOfDifferentMessagesBd = new BigDecimal(totalLengthOfDifferentMessages);
            BigDecimal postedDifferentMessageNoBd = new BigDecimal(postedDifferentMessageNo);
            BigDecimal d = totalLengthOfDifferentMessagesBd.divide(postedDifferentMessageNoBd,2,RoundingMode.HALF_UP);
            averageLength=d.floatValue();
        }
        return StatisticsDto.builder()
                .postedMessages(messageListSize <= 0 ? null : messageListSize)// Return nothing when it is null
                .occurrences(messageListSize <= 0 ? null : occurrenceList)// Return nothing when it is null
                .averageLength(averageLength).build();
    }

    /**
     * Convert map to list and make the list of occurrence and sort them by occurrence.
     * We can see at the top: 3->2->1 , We can see at the down (1)
     * @return list of Occurrence
     */
    private List<OccurrenceDto> toOccurrenceSortedList(Map<String, Long> occurrencesMap) {
        return occurrencesMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))//Sort by value
                .map(x -> new OccurrenceDto(x.getKey(), x.getValue().intValue())).collect(Collectors.toList());
    }

    /**
     * Filter the list and find last minutes message. It helps us to process less messages.
     * @return message list
     */
    synchronized List<Message> getMessageListOfLastMinute() {
        long currentTimeSecond = System.currentTimeMillis() / 1000;//Get current time
        long currentTimeSecondLastMinute = currentTimeSecond - 60;// Find when was the last minute.
        return getMessages().stream().filter(message -> message.getReceivedAt() > currentTimeSecondLastMinute)
                .collect(Collectors.toList());
    }

    /**
     * Split messages by none alphanumeric characters
     * then flat the list of Splitted message list.
     * @return list of messages now
     */
    private List<String> getListOfNoneAlphanumericMessages(List<Message> messageListOfLastMinute) {
        List<List<String>> listSeperatedBy = new ArrayList<>();
        for (Message message : messageListOfLastMinute) {
            // Split messages by none alphanumeric characters
            listSeperatedBy.add(Arrays.asList(message.getMessage().split("[^\\w']+")));
        }
        //flat the list of Splitted message list. We have a list of messages now.
        return listSeperatedBy.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
