package com.shahinnazarov.edd.krm.container.dto;

import java.util.Collection;
import lombok.Data;

@Data
public class SendEmailDto {
    private Collection<String> recipients;
    private String content;
    private String eventId;

}
