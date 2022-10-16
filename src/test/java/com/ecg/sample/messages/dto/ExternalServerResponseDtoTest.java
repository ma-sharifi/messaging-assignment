package com.ecg.sample.messages.dto;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
public class ExternalServerResponseDtoTest {

    @Test
    public void shouldReturnExternalServerResponseDtoDto(){
        ExternalServerResponseDto dto=new ExternalServerResponseDto("ok");
        assertEquals("ok",dto.getResult());
    }

}