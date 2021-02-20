package com.horizon.testserver.concurrency;

import java.time.LocalDateTime;
import com.horizon.testserver.dto.FloatBooleanPairArgs;
import java.util.List;

public class Result
{
    private String name;
    private String timestamp;
    private boolean res;
    private long threadId;
    private List<FloatBooleanPairArgs> response;

    Result(String name, long threadId, List<FloatBooleanPairArgs> response) {
        super();
        this.name = name;
        this.timestamp = LocalDateTime.now().toString();
        this.response = response;
        this.threadId = threadId;
    }

    List<FloatBooleanPairArgs> getResponse() {
        return this.response;
    }

    @Override
    public String toString() {
        String format = "[ %1$-10s ] --- threadId:%2$3s,  timestamp:%3$25s";
        return String.format(format, this.name, this.threadId, this.timestamp);
    }
}