package com.horizon.testserver.service;

import com.horizon.testserver.concurrency.BaseTask;
import com.horizon.testserver.concurrency.Executor;
import com.horizon.testserver.dto.PerfItrArgs;
import com.horizon.testserver.dto.PerfResultArgs;
import com.horizon.testserver.dto.PerfTaskArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BenchmarkingService {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkingService.class);

    private Executor executor;
    private List<BaseTask> taskList;
    private Map<String, Integer[]> itrList;

    public void initBenchmarking(PerfTaskArgs taskParam) throws Exception {

        taskList = new ArrayList<>(taskParam.getIterationParam().size());
        itrList = new HashMap<>(taskParam.getIterationParam().size());
        for (PerfItrArgs param: taskParam.getIterationParam()) {
            BaseTask task;
            Class<?> c;
            try{
                c = Class.forName(param.getName()) ;
                Constructor<?> cons = c.getDeclaredConstructor(new Class[] { PerfItrArgs.class });
                task = (BaseTask)cons.newInstance(new Object[]{param});

            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }
            itrList.put(param.getName(), new Integer[] { param.getIteration(), param.getWarmUpIteration() });
            taskList.add(task);
        }
        if (taskList.size() == 0) {
            logger.error("There is no task in the list. So it can't be processed.");
            throw new Exception("There is no task in the list. So it can't be processed.");
        }
    }

    @Async("threadPoolTaskExecutor")
    public void processTasks(PerfTaskArgs taskParam) {

        if (taskParam.getThreadPoolSize() > 0) {
            executor = new Executor(taskParam.getThreadPoolSize(), taskParam.getThreads(),
                    taskParam.getWarmUpThreads(), itrList);
        } else {
            executor = new Executor(taskParam.getThreads(), taskParam.getWarmUpThreads(), itrList);
        }

        logger.info("Start processing tasks ...");
        executor.processTasks(taskList);
    }

    public PerfResultArgs getPerfResult() throws Exception {
        if (executor == null) {
            throw new Exception("Benchmarking has not started.");
        }

        if (executor.getPerfResult() == null) {
            throw new Exception("Benchmarking is in progress ... ");
        }

        return executor.getPerfResult();
    }
}
