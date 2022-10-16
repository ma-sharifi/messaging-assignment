package com.ecg.sample.messages.dto;

import com.ecg.sample.messages.model.Message;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
public class StatisticsDtoTest {

    @Test
    public void shouldReturnStatisticsDtoTest(){
        StatisticsDto dto=StatisticsDto.builder()
                .averageLength(6.67F)
                .postedMessages(3)
                .occurrences(List.of(new OccurrenceDto("test",2), new OccurrenceDto("message1",1)))
                .build();
        assertEquals(3, dto.getPostedMessages().intValue());
        assertEquals(6.67F, dto.getAverageLength(),0.01);
        assertEquals(2, dto.getOccurrences().size());
        assertThat(dto.getOccurrences(), containsInAnyOrder(new OccurrenceDto("test",2)
                ,new OccurrenceDto("message1",1)));
    }

}