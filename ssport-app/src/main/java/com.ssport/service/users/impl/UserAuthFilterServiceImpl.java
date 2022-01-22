package com.ssport.service.users.impl;

import com.ssport.service.users.UserAuthFilterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserAuthFilterServiceImpl implements UserAuthFilterService {

    private final int limit;
    private String limitAsString;
    private ConcurrentHashMap<String, Integer> userMap;

    public UserAuthFilterServiceImpl(@Value("${prymetime.requests-per-minute-limit}")
                                             String limitAsString) {
        this.limit = Integer.parseInt(limitAsString);
        userMap = new ConcurrentHashMap<>();
    }

    @Scheduled(fixedRate = 60000)
    private void clearMap() {
        userMap.clear();
    }

    @Override
    public boolean filterByRequestAmount(String id) {
        return userMap.compute(id, (k, v) -> (v == null) ? 1 : v + 1) <= limit;
    }
}
