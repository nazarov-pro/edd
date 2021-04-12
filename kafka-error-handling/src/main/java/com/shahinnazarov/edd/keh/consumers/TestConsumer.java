package com.shahinnazarov.edd.keh.consumers;

import com.shahinnazarov.edd.keh.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component(Constants.BEAN_KAFKA_TEST_CONSUMER_NAME)
@Slf4j
public class TestConsumer implements MessageListener<String, String> {

    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        log.info("topic: {}, key: {}, value: {}", data.topic(), data.key(), data.value());
        if(data.key().equals("5")) {
            throw new RuntimeException();
        }
    }
}
