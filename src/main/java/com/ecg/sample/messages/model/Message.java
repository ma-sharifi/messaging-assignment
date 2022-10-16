package com.ecg.sample.messages.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class Message {

    private final String message;

    @JsonIgnore
    private final Long receivedAt = (System.currentTimeMillis() / 1000); //in Seconds. Helps for statisctics

    @JsonCreator
    public Message(@JsonProperty("message") String message) {
        this.message = message;
    }

    public Long getReceivedAt() {
        return receivedAt;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return this.message.equals(message.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }


}
