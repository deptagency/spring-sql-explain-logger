package com.deptagency.sqlexplain.execute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PostgreSQLExplainPlanQueryCreator implements ExplainPlanQueryCreator {

  Logger logger = LoggerFactory.getLogger(PostgreSQLExplainPlanQueryCreator.class);

  public PostgreSQLExplainPlanQueryCreator() {

  }
  
  /** 
   * Create an explain plan query for a PostgreSQL database
   * @param query to create an explain plan for
   * @return String the explain plan query
   */
  @Override
  public String getExplainPlanQuery(final String query) {
    String explainQuery = "EXPLAIN (FORMAT JSON) " + query;
    return explainQuery;
  }

}
