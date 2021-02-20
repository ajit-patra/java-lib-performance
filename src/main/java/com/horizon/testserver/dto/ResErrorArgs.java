package com.horizon.testserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResErrorArgs {
    private int errorCode;
    private String description;
}
