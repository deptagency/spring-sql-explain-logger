# DEPT SQL Explain
SQL explain is an interceptor that outputs explain plans for sql queries.

## Example outpot

```json
{
"Query": "select customer0_.id as id1_0_0_, 
            customer0_.first_name as first_na2_0_0_, 
            customer0_.last_name as last_nam3_0_0_ 
        from 
            customer customer0_ where customer0_.id=?"  
}
 [
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
```

## Maven
```xml
<dependency>
	<groupId>com.deptagency</groupId>
	<artifactId>sql-explain</artifactId>
	<version>[LATEST_VERSION]</version>
</dependency>
```

## Configuration
Add the config property below and set value to true to enable explain plan logging.
```
com.deptagency.sqlexplain.enabled=true
```

## Supported databases

- PostgreSQL
- MySQL