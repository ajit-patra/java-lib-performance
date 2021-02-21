package com.horizon.testserver.concurrency;

import java.time.LocalDateTime;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Result
{
    private final String name;
    private final String timestamp;
    private final long threadId;
    private final List<Pair<Float, Boolean>> response;

    Result(String name, long threadId, List<Pair<Float, Boolean>> response) {
        super();
        this.name = name;
        this.timestamp = LocalDateTime.now().toString();
        this.response = response;
        this.threadId = threadId;
    }

    List<Pair<Float, Boolean>> getResponse() {
        return this.response;
    }

    @Override
    public String toString() {
        String format = "[ %1$-10s ] --- threadId:%2$3s,  timestamp:%3$25s";
        return String.format(format, this.name, this.threadId, this.timestamp);
    }
}
