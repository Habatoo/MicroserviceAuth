package com.ssport;

import com.ssport.util.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@Profile({"local", "demo"})
public class SportApplication {

    public static void main(String[] args) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(timeZone);
        Locale.setDefault(Constants.DEFAULT_LOCALE);
        SpringApplication.run(SportApplication.class, args);
    }
}
