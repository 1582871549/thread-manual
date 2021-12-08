package com.rice.meng.service.decorator;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class UserComputer implements Computable<String, Integer> {

    @Override
    public Integer compute(String userId) {
        log.info("the user is calculating...");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("complete calculation!");
        return Integer.valueOf(userId);
    }
}
