package com.shahinnazarov.edd.krm.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shahinnazarov.edd.krm.container.dto.SendEmailDto;
import com.shahinnazarov.edd.krm.services.db.EmailMessageService;
import com.shahinnazarov.edd.krm.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service(Constants.BEAN_SEND_EMAIL_CONSUMER)
@RequiredArgsConstructor
@Slf4j
public class SendEmailConsumer implements MessageListener<String, String> {
    private final EmailMessageService emailMessageService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        log.info("Send email event received: {}", data.value());
        try {
            emailMessageService.save(
                    objectMapper.readValue(data.value(), SendEmailDto.class)
            );
        } catch (JsonProcessingException e) {
            log.error("json error, ", e);
        }
    }
}
