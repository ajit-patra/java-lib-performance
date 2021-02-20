package com.horizon.testserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringObjectPairArgs {
    String type;
    Object value;
}
