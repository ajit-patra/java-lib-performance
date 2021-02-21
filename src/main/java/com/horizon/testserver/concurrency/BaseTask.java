package com.horizon.testserver.concurrency;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class BaseTask implements Callable<Result> {

    private final String name;
    private final int iteration;
    private final int warmUpIteration;

    public BaseTask(String name, int iteration, int warmUpIteration) {
        this.name = name;
        this.iteration = iteration;
        this.warmUpIteration = warmUpIteration;
    }

    @Override
    public synchronized Result call() {
        Result res = null;
        try {
            int count = 0;

            while (this.warmUpIteration > count) {
                count++;
                doTask();
            }

            boolean response;
            long start;
            float respTime;
            List<Pair<Float, Boolean>> responseTime1 = new ArrayList<>();
            String template = "[ %1$-10s ] --- threadId:%2$3s,  timestamp:%3$25s,  responseTime (ms):%4$13s,  result:%5$5s";

            count = 0;
            while (this.iteration > count) {
                start = System.nanoTime();
                count++;
                response = doTask();
                respTime = (float) (System.nanoTime() - start)/1000000;
                responseTime1.add(Pair.of(respTime, response));

                System.out.printf((template) + "%n", this.name, Thread.currentThread().getId(),
                        LocalDateTime.now().toString(), respTime, response);
            }

            res = new Result(this.name, Thread.currentThread().getId(), responseTime1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    protected abstract boolean doTask();
}
