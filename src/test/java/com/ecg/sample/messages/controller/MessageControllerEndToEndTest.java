package com.ecg.sample.messages.controller;

import com.ecg.sample.messages.dto.OccurrenceDto;
import com.ecg.sample.messages.dto.StatisticsDto;
import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.singleton.MessageSingleton;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class MessageControllerEndToEndTest {

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
    public void shouldReturn200_whenAddMessageIsCalled() {
        String resourceUrl = uri + "/messages";
        HttpEntity<Message> request = new HttpEntity<>(new Message("test message"));
        ResponseEntity responseEntity = this.restTemplate.postForEntity(resourceUrl, request, ResponseEntity.class);

        assertEquals(200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldReturn200AndMessageList_whenGetMessageListIsCalled() {
        MessageSingleton.INSTANCE.clearList();
        String resourceUrl = uri + "/messages" ;
        HttpEntity<Message> request1 = new HttpEntity<>(new Message("test message"));
        ResponseEntity responseEntity1 = this.restTemplate.postForEntity(resourceUrl, request1, ResponseEntity.class);
        assertEquals(200, responseEntity1.getStatusCode().value());

        ResponseEntity<List>  responseEntity  =restTemplate.getForEntity(resourceUrl, List.class);

        assertEquals(200, responseEntity.getStatusCode().value());
        List<Message> messageList = responseEntity.getBody();
        assertNotNull(messageList);
        assertEquals(1, messageList.size());
        Assert.assertTrue(messageList.toString().contains("test message"));

    }

}
