package com.rice.meng.service.executor.impl;

import com.rice.meng.service.executor.Executor;

public class SingleThreadTaskExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
