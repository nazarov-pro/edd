package com.shahinnazarov.edd.keh.configs;

import static com.shahinnazarov.edd.keh.utils.Constants.BEAN_KAFKA_TEST_CONSUMER_CONTAINER_NAME;

import com.shahinnazarov.edd.keh.consumers.TestConsumer;
import com.shahinnazarov.edd.keh.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConf {
    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerStringStringProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test_group_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        return props;
    }

    @Bean(BEAN_KAFKA_TEST_CONSUMER_CONTAINER_NAME)
    public ConcurrentMessageListenerContainer<String, String> testConsumer(
            @Qualifier(Constants.BEAN_KAFKA_TEST_CONSUMER_NAME)
                    TestConsumer testConsumer,
            KafkaOperations<String, String> kafkaTemplate
    ) {
        Integer concurrency = 10;
        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConcurrency(concurrency);
        listenerContainerFactory
                .setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerStringStringProperties()));
        listenerContainerFactory.setErrorHandler(
                new SeekToCurrentErrorHandler(
                        deadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(10000, 3)
                )
        );
        ConcurrentMessageListenerContainer<String, String> container = listenerContainerFactory
                .createContainer(Constants.TEST_TOPIC);
        container.setAutoStartup(false);
        container.setBeanName("ID");
        container.setupMessageListener(testConsumer);
        return container;
    }

    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaOperations<String, String> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    return new TopicPartition("topic-fail", -1);
                });
    }

}
