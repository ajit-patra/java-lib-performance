package com.horizon.testserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerfItrArgs {
    private String name;
    private int iteration;
    private int warmUpIteration;
    private String data;
}
