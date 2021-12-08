package com.rice.meng.service.executor;

public class SingleThreadTaskExecutor implements Executor{

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
