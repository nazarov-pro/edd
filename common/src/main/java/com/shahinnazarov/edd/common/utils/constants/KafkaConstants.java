package com.shahinnazarov.edd.common.utils.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaConstants {
    public static final String TOPIC_SEND_EMAIL = "edd.send.email";
    public static final String TOPIC_EMAIL_NOTIFICATION = "postgres.public.t_email_message_events";

    public static final String TOPIC_SEND_EMAIL_ONE = "edd.send.email-1";
    public static final String TOPIC_SEND_EMAIL_TWO = "edd.send.email-2";

}
