package com.ecg.sample.messages.controller;

import com.ecg.sample.messages.dto.OccurrenceDto;
import com.ecg.sample.messages.dto.StatisticsDto;
import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.singleton.MessageSingleton;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsControllerEndToEndTest {

    private String uri;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @PostConstruct
    public void init() {
        uri = "http://localhost:" + port;
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void shouldReturn200AndStatisticsDto_whenStatisticsIsCalled() {
        String resourceUrl = uri + "/stats";
        MessageSingleton.INSTANCE.clearList();
        MessageSingleton.INSTANCE.addMessage(new Message("test message1"));
        MessageSingleton.INSTANCE.addMessage(new Message("test message2"));

        ResponseEntity<StatisticsDto> responseEntity = this.restTemplate.getForEntity(resourceUrl, StatisticsDto.class);

        assertEquals(200, responseEntity.getStatusCode().value());
        StatisticsDto statisticsDto = responseEntity.getBody();
        assertNotNull(statisticsDto);

        assertEquals(2, statisticsDto.getPostedMessages().intValue());
        assertEquals(6.67, statisticsDto.getAverageLength(), 0.01);
        assertEquals(3, statisticsDto.getOccurrences().size());
        Assert.assertThat(statisticsDto.getOccurrences(), containsInAnyOrder(new OccurrenceDto("test", 2)
                , new OccurrenceDto("message1", 1), new OccurrenceDto("message2", 1)));
    }
}
