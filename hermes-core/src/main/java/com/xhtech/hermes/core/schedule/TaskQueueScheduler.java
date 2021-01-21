package com.xhtech.hermes.core.schedule;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskQueueScheduler {

    private final Logger logger = LoggerFactory.getLogger(TaskQueueScheduler.class);

    public static final int DEFAULT_EVENT_LOOP_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    private static final StopTask STOP_TASK = new StopTask();

    private static final String DEFAULT_NAME = TaskQueueScheduler.class.getSimpleName();

    private String name;

    private int queueSize = -1;

    private int poolSize = 0;

    private LinkedBlockingDeque<Task> queue;

    private Map<String, Task> queueing = new ConcurrentHashMap<>();

    private ThreadPoolExecutor executor;

    private LinkedBlockingQueue consumerQueue = new LinkedBlockingQueue();

    private AtomicBoolean running = new AtomicBoolean(false);

    private CountDownLatch stopLatch;

    public TaskQueueScheduler() {
        this(-1, true);
    }

    public TaskQueueScheduler(String name) {
        this(DEFAULT_EVENT_LOOP_THREADS, -1, true, name);
    }

    public TaskQueueScheduler(int poolSize, String name) {
        this(poolSize,-1, true, name);
    }

    public TaskQueueScheduler(int poolSize, int queueSize) {
        this(poolSize, queueSize, true);
    }

    public TaskQueueScheduler(int poolSize, boolean start) {
        this(poolSize, -1, start);
    }

    public TaskQueueScheduler(int poolSize, boolean start, String name) {
        this(poolSize, -1, start, name);
    }

    public TaskQueueScheduler(int poolSize, int queueSize, boolean start) {
        this(queueSize, start, DEFAULT_NAME);
    }

    public TaskQueueScheduler(int poolSize, int queueSize, boolean start, String name) {
        this.poolSize = poolSize;
        this.queueSize = queueSize;
        this.name = name;
        this.queue = queueSize > 0 ? new LinkedBlockingDeque(queueSize) : new LinkedBlockingDeque();
        this.executor = createExecutor(name);

        if (start) {
            start();
        }
    }

    private ThreadPoolExecutor createExecutor(String name) {
        BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern(name + "-thread-%d").priority(Thread.MAX_PRIORITY).build();
        return new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, consumerQueue, factory);
    }

    public synchronized void add(Task task) throws TaskRejectedException {
        if (queueSize != -1 && queue.size() > queueSize) {
            throw new TaskRejectedException("The queue is full");
        }

        if (task == STOP_TASK || enqueueing(task)) {
            queue.add(task);
        } else {
            logger.info("Task processing in queue");
        }
    }

    public synchronized void addFirst(Task task) throws TaskRejectedException {
        if (queueSize != -1 && queue.size() > queueSize) {
            throw new TaskRejectedException("The queue is full");
        }

        if (enqueueing(task)) {
            queue.addFirst(task);
        } else {
            logger.info("Task processing in queue");
        }
    }

    private boolean enqueueing(Task task) {
        return queueing.putIfAbsent(task.id(), task) == null;
    }

    private boolean dequeueing(Task task) {
        return queueing.remove(task.id()) != null;
    }

    private Task dequeueing(String id) {
        return queueing.remove(id);
    }

    public Task getQueueingTask(String id) {
        Task task = dequeueing(id);
        return task != null ? task.expire() : null;
    }

    public int size() {
        return queue.size();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            stopLatch = new CountDownLatch(poolSize);

            for (int i = 0; i < poolSize; i++) {
                executor.submit(new TaskQueueConsumer());
            }
        }
    }

    public void stop() {
        logger.info("try stop...");
        if (running.compareAndSet(true, false)) {
            logger.info("it's in running");
            for (int i = 0; i < poolSize; i++) {
                add(STOP_TASK);
            }

            try {
                logger.info("start await");
                stopLatch.await();
                logger.info("end await");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("finish stop...");
    }

    public void shutdown() {
        stop();
        executor.shutdown();
    }

    public boolean isRunning() {
        return running.get();
    }

    class TaskQueueConsumer implements Runnable {
        @Override
        public void run() {
            try {
                Task task;
                while (running.get() && (task = queue.take()) != null && task != STOP_TASK) {
                    if (dequeueing(task) && task.expire() != null) {
                        try {
                            task.execute();
                        } catch (Throwable e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            } finally {
                logger.warn("{}[{}] is shutdown", name, Thread.currentThread().getName());
                stopLatch.countDown();
            }
        }
    }

    static class StopTask extends Task {
        @Override
        public void execute() {
        }
    }
}
