package com.horizon.testserver.controller;

import com.horizon.testserver.concurrency.Executor;
import com.horizon.testserver.dto.ResErrorArgs;
import com.horizon.testserver.dto.PerfItrArgs;
import com.horizon.testserver.dto.PerfResultArgs;
import com.horizon.testserver.dto.PerfTaskArgs;
import com.horizon.testserver.service.BenchmarkingService;
import com.horizon.testserver.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BenchmarkingController {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkingController.class);

    @Autowired
    private BenchmarkingService benchmarkService;

    @RequestMapping(value = "/benchmarking/start", method = RequestMethod.POST)
    public ResponseEntity<?> startBenchmarking(@RequestBody PerfTaskArgs taskParam) {

        try {
            logger.info("Inside benchmarking");
            if (Executor.getPerfProgress()) {
                throw new Exception("Benchmarking is in progress");
            }

            JsonUtil.prettyPrint("Request Data", taskParam);

            validateTaskParam(taskParam);
            for (PerfItrArgs param: taskParam.getIterationParam()) {
                validateIterationParam(param);
                param.setName("com.horizon.testserver.concurrency.tasks." + param.getName());
            }
            benchmarkService.initBenchmarking(taskParam);
            benchmarkService.processTasks(taskParam);

        } catch (Exception ex) {
            ResErrorArgs errorParams = new ResErrorArgs(500, ex.getMessage());
            return new ResponseEntity<>(errorParams, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateTaskParam(PerfTaskArgs taskParam) throws Exception {

        if (taskParam.getWarmUpThreads() < 0) {
            throw new Exception("PerfTaskParam warm up is less than 0");
        }
        if (taskParam.getThreads() < 1) {
            throw new Exception("PerfTaskParam no of threads is less than 1");
        }
        if (taskParam.getThreadPoolSize() < 0) {
            throw new Exception("PerfTaskParam thread pool size is less than 0");
        }
    }

    private void validateIterationParam(PerfItrArgs iterationParam) throws Exception {
        if (iterationParam.getIteration() < 1 ) {
            throw new Exception("PerfTaskParam iteration is less than 1");
        }
        if (iterationParam.getWarmUpIteration() < 0 ) {
            throw new Exception("PerfTaskParam warmUpIteration is less than 0");
        }
        if (iterationParam.getWarmUpIteration() > 100 ) {
            throw new Exception("PerfTaskParam warmUpIteration is greater than 100");
        }
        if (iterationParam.getData() == null) {
            throw new Exception("PerfTaskParam data field is null");
        }
        if (iterationParam.getData().length() == 0) {
            throw new Exception("PerfTaskParam data length is 0");
        }
        if (!iterationParam.getName().endsWith("Task")) {
            throw new Exception("PerfTaskParam name is not a valid Task class");
        }
    }

    @RequestMapping(value = "/benchmarking/result", method = RequestMethod.GET)
    public ResponseEntity<?> getResult() {

        try {
            PerfResultArgs result = benchmarkService.getPerfResult();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            ResErrorArgs errorParams = new ResErrorArgs(500, ex.getMessage());
            return new ResponseEntity<>(errorParams, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
