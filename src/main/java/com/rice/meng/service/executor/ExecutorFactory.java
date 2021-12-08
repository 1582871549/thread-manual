package com.rice.meng.service.executor;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ExecutorFactory {

    private static final String EXECUTOR_CONFIG_FILE_NAME = "executorConfig.properties";
    private static final String EXECUTOR_TYPE = "executorType";

    private static ExecutorTypeEnum type;

    static {
        readPropertiesFromConfigFile();
    }

    public static Executor newExecutor() {
        switch (type) {
            case SINGLE_THREAD:
                return new SingleThreadTaskExecutor();
            case THREAD_PER_TASK:
                return new ThreadPerTaskExecutor();
            default:
                return new SingleThreadTaskExecutor();
        }
    }

    private static void readPropertiesFromConfigFile() {
        try (InputStream input = ExecutorFactory.class.getClassLoader().getResourceAsStream(EXECUTOR_CONFIG_FILE_NAME)) {

            Properties executorProps = new Properties();
            executorProps.load(input);

            String property = executorProps.getProperty(EXECUTOR_TYPE);
            type = ExecutorTypeEnum.getExecutorType(property);

            log.info("executorType is " + type);
        } catch (Exception e) {
            log.error("Load properties failed!", e);
        }
    }

    enum ExecutorTypeEnum {

        SINGLE_THREAD("singleThread"),
        THREAD_PER_TASK("threadPerTask");

        private final String name;

        ExecutorTypeEnum(String name) {
            this.name = name;
        }

        public static ExecutorTypeEnum getExecutorType(String typeName) {
            for (ExecutorTypeEnum executorTypeEnum : ExecutorTypeEnum.values()) {
                if (executorTypeEnum.name.equals(typeName)) {
                    return executorTypeEnum;
                }
            }
            return null;
        }
    }
}
