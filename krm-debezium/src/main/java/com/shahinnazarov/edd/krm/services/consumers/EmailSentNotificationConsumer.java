package com.shahinnazarov.edd.krm.services.consumers;

import com.shahinnazarov.edd.krm.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service(Constants.BEAN_SEND_EMAIL_NOTIFICATION_CONSUMER)
@RequiredArgsConstructor
@Slf4j
public class EmailSentNotificationConsumer implements MessageListener<String, String> {

    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        log.info("Send email event received: value: {}, key: {}", data.value(), data.key());
    }
}
