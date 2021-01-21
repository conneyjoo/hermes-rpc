package com.xhtech.hermes.core.schedule;

import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by louis on 2020/10/13.
 */
@Slf4j
public class TaskQueueSchedulerTest {

    private TaskQueueScheduler taskQueueScheduler;
    public void addTask(Task task) {
        if (taskQueueScheduler == null) {
            synchronized (this) {
                if (taskQueueScheduler == null) {
                    taskQueueScheduler = new TaskQueueScheduler(64, getClass().getSimpleName());
                }
            }
        }

        taskQueueScheduler.add(task);
    }


    @Test
    public void test(){
        taskQueueScheduler = new TaskQueueScheduler(64, getClass().getSimpleName());
        taskQueueScheduler.start();
        for (int i = 0; i < 10; i++) {
            final int k = i;
            taskQueueScheduler.add(new Task() {
                @Override
                public void execute() {
                    doSth(2 + k);
                }
            });
        }

        log.info("start stop");

        taskQueueScheduler.stop();
        log.info("end stop");


    }

    private void doSth(long ms){
        try {

            log.info("start {}", ms);
            Thread.sleep(ms);
            log.info("end {}", ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}