package com.shahinnazarov.edd.krm.configurations;

import static com.shahinnazarov.edd.krm.utils.Constants.BEAN_SEND_EMAIL_CONSUMER;
import static com.shahinnazarov.edd.krm.utils.Constants.BEAN_SEND_EMAIL_LISTENER_CONTAINER;

import com.shahinnazarov.edd.common.utils.constants.KafkaConstants;
import com.shahinnazarov.edd.krm.services.consumers.SendEmailConsumer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;

@Configuration
@Slf4j
public class KafkaConsumerConfiguration {
    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${app.kafka.consumer.group-id}")
    private String groupId;
    @Value("${app.kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;
    @Value("${app.kafka.consumer.isolation-level}")
    private String isolationLevel;

    public Map<String, Object> consumerStringStringProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);
        return props;
    }

    @Bean(BEAN_SEND_EMAIL_LISTENER_CONTAINER)
    public ConcurrentMessageListenerContainer<String, String> emailSendConsumerContainer(
            @Qualifier(BEAN_SEND_EMAIL_CONSUMER) SendEmailConsumer sendEmailConsumer,
            ChainedKafkaTransactionManager<String, String> chainedKafkaTransactionManager
    ) {
        Integer concurrency = 1;
        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConcurrency(concurrency);
        listenerContainerFactory
                .setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerStringStringProperties()));
        listenerContainerFactory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);
        listenerContainerFactory.setErrorHandler(
                (thrownException, data) -> {
                    log.error("Kafka consumer exception, ", thrownException);
                }
        );
        ConcurrentMessageListenerContainer<String, String> container = listenerContainerFactory
                .createContainer(KafkaConstants.TOPIC_SEND_EMAIL);
        container.setAutoStartup(true);
        container.setBeanName("ID");
        container.setupMessageListener(sendEmailConsumer);
        return container;
    }
}
