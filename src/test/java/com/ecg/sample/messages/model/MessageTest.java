package com.ecg.sample.messages.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
public class MessageTest {

    @Test
    public void shouldReturnOccurrenceDto(){
        Message dto=new Message("test message1");
        assertEquals("test message1", dto.getMessage());
        assertNotNull( dto.getReceivedAt());
    }
    @Test
    public void shouldReturnTrue_whenEqualIsCalled(){
        Message dto=new Message("test message1");
        assertEquals( dto,new Message("test message1"));
//        assertTrue( dto.equals(new Message("test message1")));
    }

    @Test
    public void shouldReturnTrue_whenHashCodeIsCalled(){
        Message dto=new Message("test message1");
        assertEquals( dto.hashCode(),(new Message("test message1")).hashCode());
    }

    @Test
    public void shouldReturnTrue_whenToStringIsCalled(){
        Message dto=new Message("test message1");
        assertTrue( dto.toString().contains("test message1"));
    }
}