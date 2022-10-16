package com.ecg.sample.messages.service;

import com.ecg.sample.messages.dto.ExternalServerResponseDto;
import com.ecg.sample.messages.model.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author Mahdi Sharifi
 * @since 10/9/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExternalServerServiceMockedServerTest {

    @Autowired
    private ExternalServerService externalServerService;

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    /**
     * Our external server is only called once when externalServerService.sendMessageListToExternalServer is called.
     */
    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsMockedObject() throws Exception {
        String response = "{\"result\": \"ok\"}";
        mockServer
                .expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8181/mock_external_service")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );

        Optional<ExternalServerResponseDto> responseDtoOptional= externalServerService.sendMessageListToExternalServer(List.of(new Message("test message1")));
        mockServer.verify();//Verify all expectations met
        responseDtoOptional.ifPresent(result->
        {
            assertEquals("ok", responseDtoOptional.get().getResult());
        });
    }
}