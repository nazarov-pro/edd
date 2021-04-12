package com.shahinnazarov.edd.krm.services.db.impl;

import com.shahinnazarov.edd.common.utils.constants.KafkaConstants;
import com.shahinnazarov.edd.krm.container.dto.SendEmailDto;
import com.shahinnazarov.edd.krm.container.entities.EmailMessageEntity;
import com.shahinnazarov.edd.krm.container.entities.EventDataEntity;
import com.shahinnazarov.edd.krm.repositories.EmailMessageEventRepository;
import com.shahinnazarov.edd.krm.repositories.EmailMessageRepository;
import com.shahinnazarov.edd.krm.services.db.EmailMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailMessageServiceImpl implements EmailMessageService {
    private final EmailMessageRepository emailMessageRepository;
    private final EmailMessageEventRepository emailMessageEventRepository;

    @Transactional
    @Override
    public boolean save(SendEmailDto sendEmailDto) {
        if(emailMessageEventRepository.existsById(sendEmailDto.getEventId())) {
            log.warn("This message with {} event id, has already been executed", sendEmailDto.getEventId());
            return false;
        }

        emailMessageRepository.save(
                EmailMessageEntity.builder()
                        .content(sendEmailDto.getContent())
                        .eventId(sendEmailDto.getEventId())
                        .recipients(sendEmailDto.getRecipients().stream()
                                .reduce("", (a, b) -> String.format("%s,%s", a, b))
                        ).build()
        );
        emailMessageEventRepository.save(
                EventDataEntity.builder()
                        .id(sendEmailDto.getEventId())
                        .event_data("{}")
                        .build()
        );

        return true;
    }

}
