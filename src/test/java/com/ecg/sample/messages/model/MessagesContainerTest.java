package com.ecg.sample.messages.model;

import com.ecg.sample.messages.dto.OccurrenceDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
public class MessagesContainerTest {

    @Test
    public void shouldReturnOccurrenceDto(){
        Message message1=new Message("test message1");
        MessagesContainer messagesContainer=new MessagesContainer();
        messagesContainer.addMessage(message1);
        messagesContainer.addMessage(new Message("test message2"));

        assertEquals(2, messagesContainer.getMessages().size());
        assertThat(messagesContainer.getMessages(), containsInAnyOrder(new  Message("test message1"),new Message("test message2")));

    }
}