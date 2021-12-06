package com.rice.meng.common.threadpool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Executor ensuring that all {@link Runnable} tasks submitted are executed in order
 * using the provided {@link Executor}, and serially such that no two will ever be
 * running at the same time.
 */
public final class SerializingExecutor implements Executor, Runnable {

    /**
     * Use false to stop and true to run
     */
    private final AtomicBoolean atomicBoolean = new AtomicBoolean();

    private final Executor executor;

    private final Queue<Runnable> runQueue = new ConcurrentLinkedQueue<>();

    /**
     * Creates a SerializingExecutor, running tasks using {@code executor}.
     *
     * @param executor Executor in which tasks should be run. Must not be null.
     */
    public SerializingExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Runs the given runnable strictly after all Runnables that were submitted
     * before it, and using the {@code executor} passed to the constructor.
     */
    @Override
    public void execute(Runnable r) {
        runQueue.add(r);
        schedule(r);
    }

    private void schedule(Runnable removable) {
        if (atomicBoolean.compareAndSet(false, true)) {
            boolean success = false;
            try {
                executor.execute(this);
                success = true;
            } finally {
                // It is possible that at this point that there are still tasks in
                // the queue, it would be nice to keep trying but the error may not
                // be recoverable.  So we update our state and propagate so that if
                // our caller deems it recoverable we won't be stuck.
                if (!success) {
                    if (removable != null) {
                        // This case can only be reached if 'this' was not currently running, and we failed to
                        // reschedule.  The item should still be in the queue for removal.
                        // ConcurrentLinkedQueue claims that null elements are not allowed, but seems to not
                        // throw if the item to remove is null.  If removable is present in the queue twice,
                        // the wrong one may be removed.  It doesn't seem possible for this case to exist today.
                        // This is important to run in case of RejectedExecutionException, so that future calls
                        // to execute don't succeed and accidentally run a previous runnable.
                        runQueue.remove(removable);
                    }
                    atomicBoolean.set(false);
                }
            }
        }
    }

    @Override
    public void run() {
        Runnable r;
        try {
            while ((r = runQueue.poll()) != null) {
                try {
                    r.run();
                } catch (RuntimeException e) {
                    // log.error("Exception while executing runnable " + r, e);
                    System.out.println("Exception while executing runnable " + r);
                    e.printStackTrace();
                }
            }
        } finally {
            atomicBoolean.set(false);
        }
        if (!runQueue.isEmpty()) {
            // we didn't enqueue anything but someone else did.
            schedule(null);
        }
    }

}
