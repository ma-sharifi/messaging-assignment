package com.ecg.sample.messages.service;

import com.ecg.sample.messages.dto.ExternalServerResponseDto;
import com.ecg.sample.messages.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author Mahdi Sharifi
 * @since 10/6/22
 */

@Service
@Slf4j
public class ExternalServerService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${externalserver.url}")
    private String url;

    public Optional<ExternalServerResponseDto> sendMessageListToExternalServer(List<Message> messageList) {
        ExternalServerResponseDto result=null;
        try {
            String responseString = restTemplate.postForEntity(url, messageList, String.class).getBody();
            if (responseString != null) {
                Gson gson = new GsonBuilder().create();
                result = gson.fromJson(responseString, ExternalServerResponseDto.class);
            }
        }catch (Exception  ex){
            log.error("#Exception in calling consumer service. Message is: "+ex.getMessage());
        }
        return Optional.ofNullable(result);
    }
}
