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

  Logger logger = LoggerFactory.getLogger(ExplainPlanExecutor.class);

  private static ApplicationContext applicationContext;

  /**
   * Run explain plan for a query and return the results
   * The results will be mapped to a List (one entry for each row) of Maps (one
   * entry for each column using the column name as the key).
   * 
   * @param query to run explian plan for
   * @return Optional<List<String>> Explain plan results
   */
  public List<Map<String, Object>> executeExplainPlan(final String query,
    final ExplainPlanQueryCreator queryCreator) {

    String explainQuery = queryCreator.getExlainPlanQuery(query);

    JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);

    List<Map<String, Object>> results = jdbcTemplate.queryForList(explainQuery);

    return results;
  }

  /**
   * 
   * Run explain plan for a prepared statement query and return the results
   * The results will be mapped to a List (one entry for each row) of Maps (one
   * entry for each column using the column name as the key).
   * 
   * @param query                   to run explian plan for
   * @param preparedStetementValues parameter values for prepared statement
   * @return Optional<List<Map<String, Object>>> Explain plan results
   */
  public List<Map<String, Object>> executeExplainPlan(final String query,
    
    final List<PreparedStetementValue> preparedStetementValues, final ExplainPlanQueryCreator queryCreator) {

    // TODO find another way to get bean if possible
    JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);

    String explainQuery = queryCreator.getExlainPlanQuery(query);

    Object[] args = preparedStetementValues.stream().map(value -> value.getValue()).toArray();

    List<Map<String, Object>> results = jdbcTemplate.queryForList(explainQuery, args);

    return results;

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
