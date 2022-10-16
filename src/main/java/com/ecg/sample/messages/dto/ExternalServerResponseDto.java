package com.ecg.sample.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mahdi Sharifi
 * @since 10/5/22
 */

@Data @AllArgsConstructor @NoArgsConstructor
public class ExternalServerResponseDto {
    private String result;
}
