package com.deptagency.sqlexplain.execute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PostgreSqlExplainPlanQueryCreator implements ExplainPlanQueryCreator {

  Logger logger = LoggerFactory.getLogger(PostgreSqlExplainPlanQueryCreator.class);

  public PostgreSqlExplainPlanQueryCreator() {

  }
  
  /** 
   * Create an explain plan query for a PostgreSQL database
   * @param query to create an explain plan for
   * @return String the explain plan query
   */
  @Override
  public String getExlainPlanQuery(final String query) {
    String explainQuery = "EXPLAIN (FORMAT JSON) " + query;
    return explainQuery;
  }

}
