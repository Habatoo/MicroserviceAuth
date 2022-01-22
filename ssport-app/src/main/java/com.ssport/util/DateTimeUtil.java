package com.ssport.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DateTimeUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime getLocalDateTimeUtc() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    public static LocalDateTime getLocalDateTimeUtcOffset(int utcOffset) {
        int zoneId = utcOffset / 60;
        ZoneOffset zoneOffset;
        if (zoneId > 0) {
            zoneOffset = ZoneOffset.of("+" + zoneId);
        } else {
            zoneOffset = ZoneOffset.of(String.valueOf(zoneId));
        }

        return LocalDateTime.now(zoneOffset);
    }

    public static String userTimeFormat(LocalTime localTime, String format) {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(format);
        return DATE_TIME_FORMATTER.format(localTime);
    }

    public static List<String> formatUserTime(List<String> openingHours, String formatTime) {
        List<String> newOpeningHours = new ArrayList<>(7);
        for (String time : openingHours) {
            String timeAll = time.substring(time.indexOf(' ') + 1);
            String dayWeek = time.split(" ")[0];
            String closed = time.substring(time.indexOf(" ")).trim();
            if (closed.equals("Open 24 hours")) {
                newOpeningHours.add(time);
            } else if (closed.equals("Closed")) {
                newOpeningHours.add(time);
            } else {
                String timeOne = timeAll.substring(5, 8).trim();
                String newTimeOne = timeAll.substring(0, timeAll.indexOf(" ") + 3);
                String newTimeTwo = timeAll.substring(7).trim();

                if (timeOne.equalsIgnoreCase("AM") || timeOne.equalsIgnoreCase("PM")) {
                    timeOne = LocalTime.parse(newTimeOne, DateTimeFormatter.ofPattern("h:mm a"))
                            .format(DateTimeFormatter.ofPattern(formatTime));
                    newTimeTwo = timeAll.substring(10).trim();
                } else {
                    timeOne = timeAll.substring(0, 5).trim();
                }

                String timeTwo = timeAll.substring(timeAll.lastIndexOf(" ")).trim();

                if (timeTwo.equalsIgnoreCase("AM") || timeTwo.equalsIgnoreCase("PM")) {
                    timeTwo = LocalTime.parse(newTimeTwo, DateTimeFormatter.ofPattern("h:mm a"))
                            .format(DateTimeFormatter.ofPattern(formatTime));
                }

                String newTime = dayWeek + " " + timeOne + " - " + timeTwo;
                newOpeningHours.add(newTime);
            }

        }
        return newOpeningHours;
    }

    public static List<String> checkPlaceTimeFormat(List<String> openingHours) {
        return openingHours.stream().map(time -> {
            if(time!=null){
                String closed = time.substring(time.indexOf(" ")).trim();
                if (!closed.equals("Open 24 hours") && !closed.equals("Closed") && closed.indexOf("M") > 7) {
                    try {
                        int parsInt = Integer.parseInt(closed.substring(0, 2).trim());
                        if(parsInt > 11){
                            return time.replace("–", "PM - ");
                        }
                        return time.replace("–", "AM - ");
                    } catch (Exception e){
                        return time.replace("–", "AM - ");
                    }
                }
                return time;
            }
            return null;
        }).collect(Collectors.toList());
    }

}

