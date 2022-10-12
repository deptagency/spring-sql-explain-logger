package com.deptagency.sqlexplain.listener;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deptagency.sqlexplain.PreparedStatementValue;
import com.deptagency.sqlexplain.execute.ExplainPlanExecutor;
import com.deptagency.sqlexplain.execute.ExplainPlanQueryCreator;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

public class LogExplainPlanQueryListener implements QueryExecutionListener {

    Logger logger = LoggerFactory.getLogger(LogExplainPlanQueryListener.class);

    private static final Integer EXPLAIN_QUERY_TIMEOUT_MS = 500;

    private final Integer maxCacheSize;

    private final Integer queryCacheExpiry;

    private final SQLQueriesCache SQL_QUERIES;

    public List<QueryType> SUPPORTED_QUERY_TYPES = new ArrayList<QueryType>() {
        {
            add(QueryType.SELECT);
        }
    };

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected DatabaseDialect databaseDialect;

    /**
     * Uses default values for maxQuerySize and queryExpiration
     * @param databaseDialect - The dabase dialect i.e MySQL, postgreSQL etc
    */
    public LogExplainPlanQueryListener(DatabaseDialect databaseDialect) {
        this(databaseDialect, null, null);
    }

    /**
     * @param databaseDialect - The dabase dialect i.e MySQL, postgreSQL etc
     * @param maxCacheSize - The total number of queries to keep in cache used to determine if an explain plan has been run recently for a query
     * @param queryExpiration - The period between when an explain plan for a query should be run again
     */
    public LogExplainPlanQueryListener(DatabaseDialect databaseDialect, Integer maxCacheSize, Integer queryExpiration) {
        this.databaseDialect = databaseDialect;
        this.maxCacheSize = maxCacheSize;
        this.queryCacheExpiry = queryExpiration;
        SQL_QUERIES = new SQLQueriesCache(this.maxCacheSize, this.queryCacheExpiry);
    }

    /**
     * @param execInfo
     * @param queryInfoList
     */
    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        //Do Nothing
    }

    /**
     * Get the query details and run an explain plan and log the results
     * 
     * @param execInfo
     * @param queryInfoList
     */
    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        QueryInfo queryInfo = queryInfoList.get(0);
        if (isQueryTypeSupported(queryInfo.getQuery())) {
            runExplainPlanAndLogResults(execInfo, queryInfoList, queryInfo);
        }
    }

    public static <T> CompletableFuture<T> delayedValue(final T value,
                                                        final Duration delay) {
        final CompletableFuture<T> result = new CompletableFuture<>();
        scheduler.schedule(() -> result.complete(value), delay.toMillis(), TimeUnit.MILLISECONDS);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> withDefault(final CompletableFuture<T> cf,
                                                       final T defaultValue,
                                                       final Duration timeout) {
        return (CompletableFuture<T>) CompletableFuture.anyOf(
                cf.exceptionally(ignoredException -> defaultValue),
                delayedValue(defaultValue, timeout));
    }

    private void runExplainPlanAndLogResults(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, QueryInfo queryInfo) {
        // TODO check performance and resource implications of using completable future
        // and also handling exceptions
        scheduler.schedule(() -> {
            if (SQL_QUERIES.addQuery(queryInfo.getQuery())) {
                ExplainPlanQueryCreator queryCreator = databaseDialect.getExplainPlanQueryCreator();
                if (execInfo.getStatementType() == StatementType.PREPARED) {
                    final List<ParameterSetOperation> paramList = queryInfoList
                            .get(0)
                            .getParametersList()
                            .get(0);
                    final List<PreparedStatementValue> preparedStatementValues = getPreparedStatementValues(paramList);

                    // Execute Explain Plan
                    final List<Map<String, Object>> queryResults = new ExplainPlanExecutor()
                            .executeExplainPlan(queryInfo.getQuery(), preparedStatementValues, queryCreator);

                    // Log results
                    databaseDialect.getExplainPlanLogger()
                            .logExplainPlanResults(queryInfo.getQuery(), queryResults, logger);
                } else if (execInfo.getStatementType() == StatementType.STATEMENT) {
                    // Execute Explain Plan
                    final List<Map<String, Object>> queryResults = new ExplainPlanExecutor()
                            .executeExplainPlan(queryInfo.getQuery(), queryCreator);

                    // TODO better resutls formatting (posibbly move formating to logger class)
                    // Log results
                    databaseDialect.getExplainPlanLogger()
                            .logExplainPlanResults(queryInfo.getQuery(), queryResults, logger);
                }
            }
        }, EXPLAIN_QUERY_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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
     * @return list of prepared statements
     */
    protected List<PreparedStatementValue> getPreparedStatementValues(List<ParameterSetOperation> params) {
        List<PreparedStatementValue> paramValues = new ArrayList<PreparedStatementValue>();
        for (ParameterSetOperation param : params) {
            // TODO add checks for array values existence
            paramValues.add(new PreparedStatementValue(param.getArgs()[1], param.getMethod().getParameterTypes()[1]));
        }
        return paramValues;
    }

}
