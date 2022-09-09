package com.deptagency.sqlexplain.logger;

import org.slf4j.Logger;

public class ExplainPlanLogger {

    public static void logExplainPlanResults(String query, String explainPlanResults, Logger logger) {
        //TODO update format
        logger.info(" >>>>>> Query {} Explain Results -> {}", query, explainPlanResults);
    }

}
