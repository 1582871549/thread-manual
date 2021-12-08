package com.rice.meng.service.executor;

public class ThreadPerTaskExecutor implements Executor{
    
    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}
