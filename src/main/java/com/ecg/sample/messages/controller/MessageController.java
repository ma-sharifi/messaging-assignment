package com.ecg.sample.messages.controller;

import com.ecg.sample.messages.model.Message;
import com.ecg.sample.messages.singleton.MessageSingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class MessageController {

    /**
     * adds a new message
     *
     * @return
     */
    @PostMapping(value = "/messages")
    public ResponseEntity<Void> addMessageToList(@RequestBody Message message) {
        MessageSingleton.INSTANCE.addMessage(message);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * returns the list of the added messages
     *
     * @return
     */
    @GetMapping("/messages")
    public List<Message> showMessages() {
        return MessageSingleton.INSTANCE.getMessages();
    }

    @GetMapping("/size")
    public long getSize() {
        log.info("#call list size");
        return MessageSingleton.INSTANCE.getMessages().size();
    }

}
