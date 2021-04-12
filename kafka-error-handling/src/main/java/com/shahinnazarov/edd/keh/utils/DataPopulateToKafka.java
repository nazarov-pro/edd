package com.shahinnazarov.edd.keh.utils;

import com.shahinnazarov.edd.keh.producers.TestProducer;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPopulateToKafka {
    private final TestProducer testProducer;
    @Qualifier(Constants.BEAN_KAFKA_TEST_CONSUMER_CONTAINER_NAME)
    private final ConcurrentMessageListenerContainer<String, String> container;

    @PostConstruct
    public void run() {
        IntStream.range(0, 10).parallel().forEach(
                i -> testProducer.send(String.format("%d index message received", i))
        );
        container.start();
    }
}
