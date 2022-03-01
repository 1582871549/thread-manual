package com.rice.meng.service.executor.impl;

import com.rice.meng.service.executor.Executor;

public class MultipleThreadTaskExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}
