package com.shahinnazarov.edd.krm.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String BEAN_SEND_EMAIL_LISTENER_CONTAINER = "email-send-consumer-container";
    public static final String BEAN_SEND_EMAIL_NOTIFICATION_LISTENER_CONTAINER = "email-send-notification-consumer" +
            "-container";
    public static final String BEAN_SEND_EMAIL_CONSUMER = "email-send-consumer";
    public static final String BEAN_SEND_EMAIL_NOTIFICATION_CONSUMER = "email-send-notification-consumer";
}
