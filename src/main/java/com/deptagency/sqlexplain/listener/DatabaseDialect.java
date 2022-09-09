package com.deptagency.sqlexplain.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.deptagency.sqlexplain.execute.ExplainPlanQueryCreator;
import com.deptagency.sqlexplain.execute.MySqlExplainPlanQueryCreator;
import com.deptagency.sqlexplain.execute.PostgreSqlExplainPlanQueryCreator;

public enum DatabaseDialect {

    POSTGRESQL("postgresql", true) {
        @Override
        public ExplainPlanQueryCreator getExplainPlanQueryCreator() {
            return new PostgreSqlExplainPlanQueryCreator();
        }
    },
    ORACLE("oracle", false),
    MYSQL("mysql", true) {
        @Override
        public ExplainPlanQueryCreator getExplainPlanQueryCreator() {
            return new MySqlExplainPlanQueryCreator();
        }
    };

    private static final Map<String, DatabaseDialect> DBNAMEMAP = new HashMap<>(values().length, 1);;

    static {
        for (DatabaseDialect db : values()) DBNAMEMAP.put(db.dbName, db);
    }
    
    private final String dbName;
    private final Boolean supported;

    private DatabaseDialect(String dbName, Boolean supported) {
        this.dbName = dbName;
        this.supported = supported;
    }

    public static Optional<DatabaseDialect> of(final String name) {
        return Optional.ofNullable(DBNAMEMAP.get(name));
    }
    
    public boolean isSupported() {return supported;}

    public ExplainPlanQueryCreator getExplainPlanQueryCreator() {
        //TODO find better solution
        return null;
    }

    public static Optional<DatabaseDialect> getDatabaseDialectByURL(String jdbcURL) {
        DatabaseDialect dbDialect = null;
        if (jdbcURL == null) {
            return Optional.ofNullable(null);
        }

        String [] splitStr = jdbcURL.split(":");
        if (splitStr != null && splitStr.length >= 2) {
            dbDialect = DBNAMEMAP.get(splitStr[1].toLowerCase());
        }
        return Optional.ofNullable(dbDialect);
    }

    //TODO add method to get supported DBs to log in the warn message

}
