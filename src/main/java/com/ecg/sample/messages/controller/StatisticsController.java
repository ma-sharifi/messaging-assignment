package com.ecg.sample.messages.controller;

import com.ecg.sample.messages.dto.StatisticsDto;
import com.ecg.sample.messages.singleton.MessageSingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mahdi Sharifi
 * @since 10/3/22
 */


@RestController
@RequestMapping("/stats")
@Slf4j
public class StatisticsController {

    /**
     * you MUST add to this application a new endpoint called /stats returning some statistics about the last posted messages;
     * the number of messages posted in the *last minute* (one minute before of this moment)
     * <p>
     * the average (mean) length of unique words in all the messages received in *last minute*
     * <p>
     * the number of occurrences of each word contained in the messages posted in the last minute;
     * the word separator is any not alphanumeric character. For example, if in the last minute
     * the application received the two messages "test message1" and "test message2", you MUST count:
     * word	count
     * test	2
     * message1	1
     * message2	1
     * During the execution of this task and for the next minute the results of the statistics endpoint (the one in Task #2) MAY not return accurate result.
     *
     * @return StatisticsDto
     */
    @GetMapping
    public StatisticsDto getStats() {
        log.info("#call get statistics");
        return MessageSingleton.INSTANCE.calculateStatistics();
    }
}
