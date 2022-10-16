package com.ecg.sample.messages.controller;

import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.singleton.MessageSingleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mahdi Sharifi
 * @since 10/7/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    @Autowired
    StatisticsController statisticsController;

    @Autowired
    private MockMvc mvc;

    @Autowired
    MessageController messageController; // TODO remove this, find a better way

    @Before
    public void shouldClearMessageList_whenClearListIsCalled() {
        MessageSingleton.INSTANCE.clearList();
        assertEquals(0,MessageSingleton.INSTANCE.getSize());
    }

    @Test
    public void givenMessage_shouldReturnStatistics() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));

        messageController.addMessageToList(new Message("test message1"));
        messageController.addMessageToList(new Message("test message2"));

        mvc.perform(MockMvcRequestBuilders.get("/stats"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.posted_messages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.average_length").value(6.67))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences[*].word").value(hasItem("test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences[*].count").value(hasItem(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences[*].word").value(hasItem("message1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences[*].word").value(hasItem("message2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences[*]", hasSize(3)));
    }



//    @Test
//    public void givenMessage_shouldReturnStatistics() {
//
//        messageController.addMessageToList(new Message("test"));
//        messageController.addMessageToList(new Message("test"));
//        messageController.addMessageToList(new Message("message1"));
//        messageController.addMessageToList(new Message("message2"));
//
//        StatisticsDto dto= messageController.getStatistics();
//        System.out.println("dto.getAverageLength(): "+dto.getAverageLength());
//        assertEquals(3,dto.getPostedMessages());
//        Assert.assertEquals(6.67F, dto.getAverageLength(),0.0f);
//        assertEquals(3,dto.getOccurrences().size());
//
//    }



}