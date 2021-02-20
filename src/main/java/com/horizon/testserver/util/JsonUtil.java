package com.horizon.testserver.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtil {

    private JsonUtil() {}

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static void prettyPrint(String message, Object obj) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        String prettyJson = gson.toJson(obj);
        logger.info("{} {} {}", message, System.getProperty("line.separator"), prettyJson);
    }
}
