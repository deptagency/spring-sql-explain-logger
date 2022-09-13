package com.deptagency.sqlexplain.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deptagency.sqlexplain.PreparedStetementValue;
import com.deptagency.sqlexplain.execute.ExplainPlanExecutor;
import com.deptagency.sqlexplain.execute.ExplainPlanQueryCreator;
import com.deptagency.sqlexplain.logger.ExplainPlanLogger;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;


public class LogExplainPlanQueryListener implements QueryExecutionListener {

    Logger logger = LoggerFactory.getLogger(LogExplainPlanQueryListener.class);

    private final Integer EXPLAIN_QUERY_IMEOUT_MS = 500;

    public List<QueryType> SUPPORTED_QUERY_TYPES = new ArrayList<QueryType>() {
        {
            add(QueryType.SELECT);
        }
    };

    protected DatabaseDialect databaseDialect;

    public LogExplainPlanQueryListener(DatabaseDialect databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    /**
     * @param execInfo
     * @param queryInfoList
     */
    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // TODO Auto-generated method stub

    }

    /**
     * Get the query details and run an explain plan and log the results
     * 
     * @param execInfo
     * @param queryInfoList
     */
    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        try {
            QueryInfo queryInfo = queryInfoList.get(0);
            if (isQueryTypeSupported(queryInfo.getQuery())) {
                //TODO check performance and resource implications of using completable future and also handling exceptions
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    Optional<List<Map<String, Object>>> queryResults = null;
                        ExplainPlanQueryCreator queryCreator = databaseDialect.getExplainPlanQueryCreator();

                        if (execInfo.getStatementType() == StatementType.PREPARED) {

                            List<ParameterSetOperation> paramList = queryInfoList.get(0).getParametersList().get(0);
                            List<PreparedStetementValue> preparedStetementValues = getPreparedStatementValues(paramList);

                            // Execute Explain Plan
                            queryResults = new ExplainPlanExecutor()
                                    .executeExplainPlan(queryInfo.getQuery(), preparedStetementValues, queryCreator);

                            // Log results if present
                            queryResults.ifPresent(results -> ExplainPlanLogger.logExplainPlanResults(queryInfo.getQuery(),
                                    results, logger));
                        } else if (execInfo.getStatementType() == StatementType.STATEMENT) {
                            // Execute Explain Plan
                            queryResults = new ExplainPlanExecutor()
                                    .executeExplainPlan(queryInfo.getQuery(), queryCreator);

                            // TODO better resutls formatting (posibbly move formating to logger class)
                            // Log results if present
                            queryResults.ifPresent(results -> ExplainPlanLogger.logExplainPlanResults(queryInfo.getQuery(),
                                    results, logger));
                        }
                }).orTimeout(EXPLAIN_QUERY_IMEOUT_MS, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Check if the type of query SELECT, INSERT etc is supported
     * 
     * @param query to check if type is supported by the explain query logger
     * @return Boolean if query is supported
     */
    protected Boolean isQueryTypeSupported(String query) {
        QueryType queryType = QueryUtils.getQueryType(query);
        return SUPPORTED_QUERY_TYPES.contains(queryType);
    }

    /**
     * @param params
     * @return List<PreparedStetementValue>
     */
    protected List<PreparedStetementValue> getPreparedStatementValues(List<ParameterSetOperation> params) {
        List<PreparedStetementValue> paramValues = new ArrayList<PreparedStetementValue>();
        for (ParameterSetOperation param : params) {
            // TODO add checks for array values existense
            paramValues.add(new PreparedStetementValue(param.getArgs()[1], param.getMethod().getParameterTypes()[1]));
        }
        return paramValues;
    }

}
