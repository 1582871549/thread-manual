/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.rice.meng.common.util;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * This class is aimed at simplifying work with {@code CompletableFuture}.
 */
public class FutureUtil {

    /**
     * Return a future that represents the completion of the futures in the provided list.
     *
     * @param futures futures to wait for
     * @return a new CompletableFuture that is completed when all of the given CompletableFutures complete
     */
    public static CompletableFuture<Void> waitForAll(List<? extends CompletableFuture<?>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Return a future that represents the completion of any future in the provided list.
     *
     * @param futures futures to wait any
     * @return a new CompletableFuture that is completed when any of the given CompletableFutures complete
     */
    public static CompletableFuture<Object> waitForAny(List<? extends CompletableFuture<?>> futures) {
        return CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]));
    }

    public static <T> CompletableFuture<T> failedFuture(Throwable t) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(t);
        return future;
    }

    public static Throwable unwrapCompletionException(Throwable ex) {
        if (ex instanceof CompletionException) {
            return ex.getCause();
        } else if (ex instanceof ExecutionException) {
            return ex.getCause();
        } else {
            return ex;
        }
    }

    /**
     * Creates a low-overhead timeout exception which is performance optimized to minimize allocations
     * and cpu consumption. It sets the stacktrace of the exception to the given source class and
     * source method name. The instances of this class can be cached or stored as constants and reused
     * multiple times.
     *
     * @param message exception message
     * @param sourceClass source class for manually filled in stacktrace
     * @param sourceMethod source method name for manually filled in stacktrace
     * @return new TimeoutException instance
     */
    public static TimeoutException createTimeoutException(String message, Class<?> sourceClass, String sourceMethod) {
        return new LowOverheadTimeoutException(message, sourceClass, sourceMethod);
    }

    private static class LowOverheadTimeoutException extends TimeoutException {
        private static final long serialVersionUID = 1L;

        LowOverheadTimeoutException(String message, Class<?> sourceClass, String sourceMethod) {
            super(message);
            setStackTrace(new StackTraceElement[]{new StackTraceElement(sourceClass.getName(), sourceMethod,
                    null, -1)});
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    public static <T> Optional<Throwable> getException(CompletableFuture<T> future) {
        if (future != null && future.isCompletedExceptionally()) {
            try {
                future.get();
            } catch (InterruptedException e) {
                return Optional.ofNullable(e);
            } catch (ExecutionException e) {
                return Optional.ofNullable(e.getCause());
            }
        }
        return Optional.empty();
    }
}
