package com.horizon.testserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReqArgs {
    private String id;
    private String className;
    private String methodName;
    private String methodType;
    private List<StringStringPairArgs> params;
    private String returnType;
}