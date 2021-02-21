package com.horizon.testserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PerfResultArgs {
    private int threads;
    private int warmUpThreads;
    private List<PerfItrResultArgs> iterationData;
    private int passCnt;
    private int failCnt;
    private float executionTime;
    private float elapsedTime;
    private float tp;
    private float mean;
    private float sd;
    private float min;
    private float p25;
    private float p50;
    private float p75;
    private float p90;
    private float p95;
    private float p99;
    private float p99_9;
    private float p99_99;
    private float max;
}
