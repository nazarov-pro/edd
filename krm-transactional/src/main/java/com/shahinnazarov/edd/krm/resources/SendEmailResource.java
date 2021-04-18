package com.shahinnazarov.edd.krm.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shahinnazarov.edd.common.utils.constants.KafkaConstants;
import com.shahinnazarov.edd.krm.container.dto.SendEmailDto;
import com.shahinnazarov.edd.krm.services.producers.SendEmailProducer;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SendEmailResource {
    private final SendEmailProducer sendEmailProducer;

    @PostMapping("/send-email")
    public ResponseEntity<Void> send(@RequestBody SendEmailDto sendEmailDto)
            throws InterruptedException, ExecutionException, JsonProcessingException {
        sendEmailProducer.send(KafkaConstants.TOPIC_SEND_EMAIL, sendEmailDto);
        return ResponseEntity.ok().build();
    }
}
