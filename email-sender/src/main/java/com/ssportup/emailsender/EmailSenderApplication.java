package com.ssportup.emailsender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@Profile({"local","demo"})
public class EmailSenderApplication {

    public static void main(String[] args) {

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(timeZone);
        Locale.setDefault(Locale.US);

        SpringApplication.run(EmailSenderApplication.class, args);
    }
}
