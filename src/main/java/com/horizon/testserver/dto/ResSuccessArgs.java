package com.horizon.testserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResSuccessArgs {
    private long elapsedTime;
    private String data;
    private String retType;
}