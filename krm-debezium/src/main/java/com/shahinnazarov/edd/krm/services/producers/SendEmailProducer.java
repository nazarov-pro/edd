package com.shahinnazarov.edd.krm.services.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shahinnazarov.edd.common.utils.constants.KafkaConstants;
import com.shahinnazarov.edd.krm.container.dto.SendEmailDto;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendEmailProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(SendEmailDto sendEmailDto) throws JsonProcessingException, ExecutionException,
            InterruptedException {
        log.info("Email message will be added to the queue for sending... recipients: {}, content: {}",
                sendEmailDto.getRecipients(), sendEmailDto.getContent()
        );
        kafkaTemplate.send(KafkaConstants.TOPIC_SEND_EMAIL, objectMapper.writeValueAsString(sendEmailDto))
                .get();
    }
}
