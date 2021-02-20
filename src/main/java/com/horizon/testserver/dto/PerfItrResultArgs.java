package com.horizon.testserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PerfItrResultArgs {
    private String name;
    private int iteration;
    private int warmUpIteration;
}
