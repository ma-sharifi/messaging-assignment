package com.ecg.sample.messages.scheduler;

import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.singleton.MessageSingleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author Mahdi Sharifi
 * @since 10/8/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskSchedulerMockServerTest {

    String response = "{\"result\": \"ok\"}";

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Value("${externalserver.url}")
    private String url;

    @Before
    public void shouldClearMessageList_whenClearListIsCalled() {
//        MockitoAnnotations.initMocks(this);
        MessageSingleton.INSTANCE.clearList();
        assertEquals(0,MessageSingleton.INSTANCE.getSize());
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    /**
     * Our external server is only called once when externalServerService.sendMessageListToExternalServer is called.
     * In this test will be called twice, because our pge size is 2 and we have 3 messages. It means we must call
     * the externalServer for twice not more.
     */
    @Test
    public void shouldReturnSize0_DataSendToExternalServiceSteps() throws Exception {
        scheduledTasks.clearPartitionedMessagesReadyForSendMap();
        int pageSize = 2;
        int sleepTime = 1000;
        MessageSingleton.INSTANCE.addMessage(new Message("test message1"));
        MessageSingleton.INSTANCE.addMessage(new Message("test message2"));
        MessageSingleton.INSTANCE.addMessage(new Message("test message3"));

        List<Message> list = MessageSingleton.INSTANCE.getMessages();
        assertEquals(3,MessageSingleton.INSTANCE.getSize());
        assertEquals(0,scheduledTasks.getPartitionedMessagesReadyForSendMapSize());

        scheduledTasks.partitionDataAnAddCopyToMap(list,pageSize);
        assertEquals(2,scheduledTasks.getPartitionedMessagesReadyForSendMapSize());

        MessageSingleton.INSTANCE.clearList();

        assertEquals(0,MessageSingleton.INSTANCE.getSize());

        int expectedCount= scheduledTasks.getPartitionedMessagesReadyForSendMapSize();

        mockServer
                .expect(ExpectedCount.times(expectedCount), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );
        scheduledTasks.tryToSendToExternalServer(1, pageSize, sleepTime);
        mockServer.verify();
        assertEquals(0,scheduledTasks.getPartitionedMessagesReadyForSendMapSize());

    }



}
