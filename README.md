# Spring Boot SQL Explain Logger

## Background

We created this library for Spring Boot applications. The aim is to allow you to configure detailed debug info for all your queries and log the results.

The debug output can then be monitored for potential issues from queries that have suboptimal queries.

This will allow you to fix bad queries right away in local development environments or test environments.  We don’t recommend using this in production, of course!

It outputs in JSON and therefore can be parsed by logging tools like Splunk.

## Output examples

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

{
  "Query": "select customer0_.id as id1_0_, customer0_.first_name as first_na2_0_, customer0_.last_name as last_nam3_0_ from customer customer0_ where customer0_.last_name=?",
  "Explain": {
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
  }
}
```

# Installation

## Maven

Add the dependency to `pom.xml`:

```xml
<dependency>
	<groupId>com.deptagency</groupId>
	<artifactId>spring-sql-explain-logger</artifactId>
	<version>0.0.3</version>
</dependency>
```

## Gradle

Make sure you've got these defined in `build.gradle`:

```
repositories {
    mavenCentral() 
}

dependencies {
    implementation 'com.deptagency:spring-sql-explain-logger:0.0.3' 
}
```

## Properties

Add the config property below wherever you usually set your application properties (for example, `src/main/resources/application.properties`) and set value to `true` to enable explain-plan logging:

```
com.deptagency.sqlexplain.enabled=true
```

Finally, add a component scan for the package to your application (for example, below `@SpringBootApplication`):
```
@ComponentScan("com.deptagency.sqlexplain")
```

# Limitations

The library only supports PostgreSQL and MySQL in Spring Boot apps.

# TODOs

- [ ] Allow customization of logging levels for different types of scans.  I.e. Only log table scans at WARN level.
- [ ] Allow a shorter version of the output when you just want to identify table scans (right now the output can get long!)
- [ ] Add Oracle support
