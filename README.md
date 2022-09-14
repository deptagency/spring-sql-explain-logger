# DEPT SQL Explain

I am sure we have all been in this boat. You add a query to an application which seems fairly simple without much worry of its performance implication. You deploy the application to production and even for a given period everything is going smooth until all of a sudden one day the application starts to run into performance issues. This then involves triaging, collecting and analyzing metrics to finally determine that the supposedly inoquous query is the culprit because it was doing a table scan on a table that kept increasing in size. Adding an index to the rescue.

What if you could have a way to catch the issue before it turns into a production issue. This was the motivation for creating the SQL Explain library. The library intercepts sql calls and executes an explain and logs the results. The results can then be monitored for potential issues from queries that have sub optimal queries.

At first it appeared simple enough especially for applications that use hibernate. We could easily get the SQL query of the prepared statement that Hibernate was executing. Then we started to look for a way to get the prepared statement bind parameters so we could execute the explain plan query. That turned into a much harder problem. Neither Hibernate nor JDBC API provided an easy way to get the parameters. One of the solutions we explored was proxying the Datasource and intercepting JDBC calls but we wanted the proxy to be as least intrusive as possible and easy to enable and disable per environment. Spring BeanPostProcessor has a great way of accomplishing this (add thx to xxx where we saw the solution). You can intercept any bean during creation and add a proxy and you can also make the proxy conditional based on a configuration property.

```java

@Component
@ConditionalOnProperty(value = "com.deptagency.sqlexplain.enabled", havingValue = "true")
public class ExplainPlanDatasourceProxyBean implements BeanPostProcessor {

......

@Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            Optional<DatabaseDialect> dbDialect = DatabaseDialect.getDatabaseDialectByURL(jdbcURL);

            if (dbDialect.isPresent() && dbDialect.get().isSupported()) {
                ProxyFactory factory = new ProxyFactory(bean);
                factory.setProxyTargetClass(true);
                factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean, dbDialect.get()));
                return factory.getProxy();
            } else {
                logger.warn("WARN database is not currently supported. Currently supported databases include {} ",
                        DatabaseDialect.getSupportedDatabases());
            }
        }
        return bean;
    }

.......
}

```

## Example Output

### PostgreSQL

```json
{
"Query": "select customer0_.id as id1_0_0_, customer0_.first_name as first_na2_0_0_, customer0_.last_name as last_nam3_0_0_ from customer customer0_ where customer0_.id=?" ,
"Explain": [
  {
    "Plan": {
      "Node Type": "Seq Scan",
      "Parallel Aware": false,
      "Async Capable": false,
      "Relation Name": "customer",
      "Alias": "customer0_",
      "Startup Cost": 0.00,
      "Total Cost": 1.01,
      "Plan Rows": 1,
      "Plan Width": 240,
      "Filter": "((last_name)::text = 'test_save'::text)"
    }
  }
]
}
```
### MySQL

```json

{"Query": "select customer0_.id as id1_0_, customer0_.first_name as first_na2_0_, customer0_.last_name as last_nam3_0_ from customer customer0_ where customer0_.last_name=?" ,"Explain": {
  "query_block": {
    "select_id": 1,
    "cost_info": {
      "query_cost": "0.95"
    },
    "table": {
      "table_name": "customer0_",
      "access_type": "ALL",
      "rows_examined_per_scan": 7,
      "rows_produced_per_join": 0,
      "filtered": "14.29",
      "cost_info": {
        "read_cost": "0.85",
        "eval_cost": "0.10",
        "prefix_cost": "0.95",
        "data_read_per_join": "367"
      },
      "used_columns": [
        "id",
        "first_name",
        "last_name"
      ],
      "attached_condition": "(`sql_explain`.`customer0_`.`last_name` = 'test_save')"
    }
  }
} }

```

## Installation

### Maven
```xml
<dependency>
	<groupId>com.deptagency</groupId>
	<artifactId>sql-explain</artifactId>
	<version>[LATEST_VERSION]</version>
</dependency>
```

### Configuration
Add the config property below and set value to true to enable explain plan logging.
```
com.deptagency.sqlexplain.enabled=true
```

## User Guide

Demo app

## Supported databases

- PostgreSQL
- MySQL