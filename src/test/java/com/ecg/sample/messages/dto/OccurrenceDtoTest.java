package com.ecg.sample.messages.dto;

import com.ecg.sample.messages.dto.OccurrenceDto;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */

public class OccurrenceDtoTest {

    @Test
    public void shouldReturnOccurrenceDtoTest(){
        OccurrenceDto dto=new OccurrenceDto("test",2);
        assertEquals("test", dto.getWord());
        assertEquals(2, dto.getCount());
    }
}