package com.shahinnazarov.edd.keh.producers;

import com.shahinnazarov.edd.keh.utils.Constants;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        try {
            kafkaTemplate.send(Constants.TEST_TOPIC, message.substring(0, 1), message).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
