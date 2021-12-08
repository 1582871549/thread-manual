package com.rice.meng.service.executor;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class TaskExecutionWebServer {

    private static final Executor exec = ExecutorFactory.newExecutor();

    public static void main(String[] args) throws IOException {
        log.info("The executor you are using is {}", exec);

        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = () -> handleRequest(connection);
            exec.execute(task);
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
