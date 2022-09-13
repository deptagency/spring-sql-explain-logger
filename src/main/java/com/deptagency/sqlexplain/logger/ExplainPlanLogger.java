package com.deptagency.sqlexplain.logger;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public class ExplainPlanLogger {

    public static void logExplainPlanResults(String query, List<Map<String, Object>> results, Logger logger) {
        //TODO update format
        logger.info("{\"Query\": \"{}\"  }", query);
        logger.info("{}",results.get(0).get("QUERY PLAN"));
    }

}
