package com.ecg.sample.messages.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MessagesContainer {

    List<Message> messages = new ArrayList<>(); // I could have used CopyOnWriteArrayList(). Because it has thread safe

    public synchronized void addMessage(Message message) { //added synchronized on it
        messages.add(message);
    }

    /**
     * when multiple threads share mutable data, each thread that reads or writes
     * the data must perform synchronization. Effective Java Item 78
     */
    public synchronized List<Message> getMessages() {//added synchronized
        return messages;
    }

    public synchronized void clear() {
        log.info("#message list cleared.");
        messages.clear();
    }

}
