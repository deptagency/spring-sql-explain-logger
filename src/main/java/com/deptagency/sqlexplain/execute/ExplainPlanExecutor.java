package com.deptagency.sqlexplain.execute;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.deptagency.sqlexplain.PreparedStetementValue;

@Component
public class ExplainPlanExecutor implements ApplicationContextAware {

  Logger logger = LoggerFactory.getLogger(PostgreSqlExplainPlanQueryCreator.class);

  private static ApplicationContext applicationContext;

  
  /** 
   * Run explain plan for a query and return the results
   * The results will be mapped to a List (one entry for each row)
   * @param query to run explian plan for
   * @return Optional<List<String>> Explain plan results
   */
  public Optional<List<Map<String, Object>>> executeExplainPlan(String query) {

    List<Map<String, Object>> results = null;
    try {

      // TODO get explain plan query based on database dialect
      ExplainPlanQueryCreator exp = new PostgreSqlExplainPlanQueryCreator();
      // TODO add logic to only run explain plan per query periodically
      String explainQuery = exp.getExlainPlanQuery(query);

      JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);

      results = jdbcTemplate.queryForList(explainQuery);
      return Optional.of(results);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Optional.ofNullable(results);
  }


  
  /** 
   * 
   * Run explain plan for a prepared statement query and return the results
   * The results will be mapped to a List (one entry for each row) of Maps (one entry for each column using the column name as the key).
   * 
   * @param query to run explian plan for
   * @param preparedStetementValues parameter values for prepared statement
   * @return Optional<List<Map<String, Object>>> Explain plan results
   */
  public Optional<List<Map<String, Object>>> executeExplainPlan(String query,
      List<PreparedStetementValue> preparedStetementValues) {

    List<Map<String, Object>> results = null;
    try {
      // TODO get explain plan query based on database dialect
      ExplainPlanQueryCreator exp = new PostgreSqlExplainPlanQueryCreator();
      String explainQuery = exp.getExlainPlanQuery(query);

      Object[] args = preparedStetementValues.stream().map(value -> value.getValue()).toArray();

      JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);

      results = jdbcTemplate.queryForList(explainQuery, args);
      return Optional.of(results);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Optional.ofNullable(results);

  }

  
  /** 
   * @param context
   * @throws BeansException
   */
  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    applicationContext = context;
  }

}
