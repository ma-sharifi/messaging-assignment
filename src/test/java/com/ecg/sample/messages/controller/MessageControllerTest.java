package com.ecg.sample.messages.controller;

import com.ecg.sample.messages.singleton.MessageSingleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Before //Methods annotated with the @Before annotation are run before each test.
    public void shouldClearMessageList_whenClearListIsCalled() {
        MessageSingleton.INSTANCE.clearList();
        assertEquals(0, MessageSingleton.INSTANCE.getSize());
    }

    @Test
    public void flowTest() throws Exception {

        // checks that at startup the messages is empty
        mvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

        // adds a message
        mvc.perform(MockMvcRequestBuilders
                        .post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"test message\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // checks that the message has been returned
        mvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"message\":\"test message\"}]")));
    }

    @Test
    public void shouldAdd1000MessageToList_whenMessagesEndpointIsCallIs() throws Exception {
        int totalRequestNo = 100;
        int threadsNo = 10; // Number of thread(users)
        int repeatUserRequestNo = totalRequestNo / threadsNo;

        // checks that at startup the messages is empty
        mvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

        ExecutorService threadPool = Executors.newFixedThreadPool(threadsNo);
        for (int j = 0; j < repeatUserRequestNo; j++) {
//      A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
            CountDownLatch latchUser = new CountDownLatch(threadsNo);
            for (int i = 0; i < threadsNo; i++) {
                threadPool.execute(() -> {
                    try {
                        mvc.perform(MockMvcRequestBuilders
                                        .post("/messages")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"message\": \"test message\"}")
                                        .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
                        latchUser.countDown();
                    } catch (Exception ignore) {
                    }
                });
            }
            latchUser.await();
        }
        // checks the list of messages does not have the right size
        mvc.perform(MockMvcRequestBuilders.get("/size"))
                .andExpect(status().isOk())
                // If you want to check multi thread issue, uncomment this line and remove synchronized from MessageContainer class.
//                .andExpect(content().string(not(totalRequestNo+"")));
                .andExpect(content().string(equalTo(totalRequestNo + "")));//If you want to check multi thread issue, comment this
        threadPool.shutdown();
    }

    @Test
    public void shouldReturnSize0_whenClearMethodIsCalled() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/size"))
                .andExpect(status().isOk())
                .andExpect(content().string(("0")));
    }
}
