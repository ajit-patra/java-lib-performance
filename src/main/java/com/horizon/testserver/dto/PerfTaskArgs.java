package com.horizon.testserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerfTaskArgs {
    private int threadPoolSize;
    private int warmUpThreads;
    private int threads;
    private List<PerfItrArgs> iterationParam;
}
