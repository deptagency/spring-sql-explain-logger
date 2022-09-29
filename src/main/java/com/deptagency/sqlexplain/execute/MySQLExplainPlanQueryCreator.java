package com.deptagency.sqlexplain.execute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MySQLExplainPlanQueryCreator implements ExplainPlanQueryCreator {

  Logger logger = LoggerFactory.getLogger(MySQLExplainPlanQueryCreator.class);

  public MySQLExplainPlanQueryCreator() {

  }
  
  /** 
   * Create an explain plan query for a MySQL database
   * @param query to create an explain plan for
   * @return String the explain plan query
   */
  @Override
  public String getExlainPlanQuery(final String query) {
    String explainQuery = "EXPLAIN FORMAT=JSON " + query;
    return explainQuery;
  }

}
