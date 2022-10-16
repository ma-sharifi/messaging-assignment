package com.ecg.sample.messages.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Mahdi Sharifi
 * @since 10/3/22
 */

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDto {

    @JsonProperty("posted_messages")
    private Integer postedMessages;

    @JsonProperty("average_length")
    private Float averageLength;

    @JsonProperty("occurrences")
    private List<OccurrenceDto> occurrences ;

}
