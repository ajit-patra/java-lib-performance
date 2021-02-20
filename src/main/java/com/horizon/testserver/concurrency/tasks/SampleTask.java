package com.horizon.testserver.concurrency.tasks;

import com.horizon.testserver.concurrency.BaseTask;
import com.horizon.testserver.dto.PerfItrArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SampleTask extends BaseTask {

    private static final Logger logger = LoggerFactory.getLogger(SampleTask.class);

    public SampleTask(PerfItrArgs taskData) throws Exception {
        super(taskData.getName(), taskData.getIteration(), taskData.getWarmUpIteration());

        if (taskData.getData().length() == 0) {
            throw new Exception("Data list size is 0");
        }
    }

    @Override
    public boolean doTask() {
        boolean res = false;
        try {
            // todo: call the SDK method to be measured
            Thread.sleep(new Random().nextInt(2000));
            res = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return res;
    }
}
