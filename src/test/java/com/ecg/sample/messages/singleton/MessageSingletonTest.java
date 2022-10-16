package com.ecg.sample.messages.singleton;

import com.ecg.sample.messages.dto.StatisticsDto;
import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.dto.OccurrenceDto;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
public class MessageSingletonTest {

    @Before
    public void shouldClearMessageListAndAdd2MessageToList() {
        MessageSingleton.INSTANCE.clearList();
        assertEquals(0,MessageSingleton.INSTANCE.getSize());

        MessageSingleton.INSTANCE.addMessage(new Message("test message1"));
        MessageSingleton.INSTANCE.addMessage(new Message("test message2"));

    }

    @Test
    public void shouldReturnMessages_whenAdd2MessageIsCalled() {
        List<Message> list= MessageSingleton.INSTANCE.getMessages();

        assertEquals(2,list.size());
        assertEquals("test message1",list.get(0).getMessage());
        assertEquals("test message2",list.get(1).getMessage());
    }

    @Test
    public void shouldReturnSize2_whenAdd2MessageIsCalled() {

        assertEquals(2,MessageSingleton.INSTANCE.getSize());
    }

    @Test
    public void shouldReturnStatisticsDto_WhenCalculateStatisticsIsCalled() {
        StatisticsDto statisticsDto = MessageSingleton.INSTANCE.calculateStatistics();
        List<OccurrenceDto> occurrenceList= statisticsDto.getOccurrences();
        assertNotNull(statisticsDto);
        assertEquals(3,occurrenceList.size());
        assertEquals(2,statisticsDto.getPostedMessages().intValue());
        assertEquals(6.67, statisticsDto.getAverageLength(),0.01);
        assertThat(occurrenceList, containsInAnyOrder(new OccurrenceDto("test",2)
        ,new OccurrenceDto("message1",1),
                new OccurrenceDto("message2",1)));
    }

    @Test
    public void shouldReturnMessageListOfLastMinutes_WhenMessageListOfLastMinuteIsCalled() {
        List<Message> list = MessageSingleton.INSTANCE.getMessageListOfLastMinute();
        assertNotNull(list);

        assertEquals(2, list.size());
        assertThat(list, containsInAnyOrder(new  Message("test message1"),new Message("test message2")));
    }



}