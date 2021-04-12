package com.shahinnazarov.edd.krm.configurations;

import static com.shahinnazarov.edd.krm.utils.Constants.BEAN_SEND_EMAIL_CONSUMER;
import static com.shahinnazarov.edd.krm.utils.Constants.BEAN_SEND_EMAIL_LISTENER_CONTAINER;
import static com.shahinnazarov.edd.krm.utils.Constants.BEAN_SEND_EMAIL_NOTIFICATION_CONSUMER;
import static com.shahinnazarov.edd.krm.utils.Constants.BEAN_SEND_EMAIL_NOTIFICATION_LISTENER_CONTAINER;

import com.shahinnazarov.edd.common.utils.constants.KafkaConstants;
import com.shahinnazarov.edd.krm.services.consumers.EmailSentNotificationConsumer;
import com.shahinnazarov.edd.krm.services.consumers.SendEmailConsumer;
import java.util.HashMap;
import java.util.Map;
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
@Slf4j
public class KafkaConsumerConfiguration {
    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerStringStringProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "edd_krm");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        return props;
    }

    @Bean(BEAN_SEND_EMAIL_LISTENER_CONTAINER)
    public ConcurrentMessageListenerContainer<String, String> emailSendConsumerContainer(
            @Qualifier(BEAN_SEND_EMAIL_CONSUMER)
                    SendEmailConsumer sendEmailConsumer,
            KafkaOperations<String, String> kafkaTemplate
    ) {
        Integer concurrency = 1;
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
                .createContainer(KafkaConstants.TOPIC_SEND_EMAIL);
        container.setAutoStartup(true);
        container.setBeanName("ID");
        container.setupMessageListener(sendEmailConsumer);
        return container;
    }

    @Bean(BEAN_SEND_EMAIL_NOTIFICATION_LISTENER_CONTAINER)
    public ConcurrentMessageListenerContainer<String, String> emailSendConsumerContainer(
            @Qualifier(BEAN_SEND_EMAIL_NOTIFICATION_CONSUMER)
            EmailSentNotificationConsumer consumer,
            KafkaOperations<String, String> kafkaTemplate
    ) {
        Integer concurrency = 1;
        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConcurrency(concurrency);
        listenerContainerFactory
                .setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerStringStringProperties()));
        listenerContainerFactory.setErrorHandler(
                (thrownException, data) -> {
                    log.error("Kafka consumer exception, ", thrownException);
                }
        );
        ConcurrentMessageListenerContainer<String, String> container = listenerContainerFactory
                .createContainer(KafkaConstants.TOPIC_EMAIL_NOTIFICATION);
        container.setAutoStartup(true);
        container.setBeanName("notification-ID");
        container.setupMessageListener(consumer);
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
