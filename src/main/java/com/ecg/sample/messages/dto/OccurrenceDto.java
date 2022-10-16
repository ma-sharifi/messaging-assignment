package com.ecg.sample.messages.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Mahdi Sharifi
 * @since 10/3/22
 */
@Data
@AllArgsConstructor
public class OccurrenceDto {
    private String word;
    private int count;
}
