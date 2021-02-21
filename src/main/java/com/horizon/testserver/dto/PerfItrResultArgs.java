package com.horizon.testserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerfItrResultArgs {
    private String name;
    private int iteration;
    private int warmUpIteration;
}
