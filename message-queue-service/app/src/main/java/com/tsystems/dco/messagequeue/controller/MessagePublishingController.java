package com.tsystems.dco.messagequeue.controller;

import com.tsystems.dco.messagequeue.api.MessagePublishingApi;
import com.tsystems.dco.messagequeue.model.MessagePublishRequest;
import com.tsystems.dco.messagequeue.model.MessagePublishResponse;
import com.tsystems.dco.messagequeue.service.MessagePublishingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessagePublishingController implements MessagePublishingApi {

    private final MessagePublishingService messagePublishingService;

    @Override
    public ResponseEntity<MessagePublishResponse> publishMessage(MessagePublishRequest messagePublishRequest) {
        log.info("Publishing message to queue: {}", messagePublishRequest.getQueueName());
        
        try {
            MessagePublishResponse response = messagePublishingService.publishMessage(messagePublishRequest);
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            log.error("Failed to publish message to queue: {}", messagePublishRequest.getQueueName(), e);
            MessagePublishResponse errorResponse = new MessagePublishResponse();
            errorResponse.setStatus(MessagePublishResponse.StatusEnum.FAILED);
            errorResponse.setTimestamp(java.time.OffsetDateTime.now());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
