package com.deptagency.sqlexplain.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.deptagency.sqlexplain.execute.ExplainPlanQueryCreator;
import com.deptagency.sqlexplain.execute.MySQLExplainPlanQueryCreator;
import com.deptagency.sqlexplain.execute.PostgreSQLExplainPlanQueryCreator;
import com.deptagency.sqlexplain.logger.DefaultExplainPlanLogger;
import com.deptagency.sqlexplain.logger.ExplainPlanLogger;
import com.deptagency.sqlexplain.logger.MySQLExplainPlanLogger;
import com.deptagency.sqlexplain.logger.PostgreSQLExplainPlanLogger;

public enum DatabaseDialect {

    POSTGRESQL("postgresql", true) {
        @Override
        public ExplainPlanQueryCreator getExplainPlanQueryCreator() {
            return new PostgreSQLExplainPlanQueryCreator();
        }

        @Override
        public ExplainPlanLogger getExplainPlanLogger() {
            return new PostgreSQLExplainPlanLogger();
         }

    },
    ORACLE("oracle", false),
    MYSQL("mysql", true) {
        @Override
        public ExplainPlanQueryCreator getExplainPlanQueryCreator() {
            return new MySQLExplainPlanQueryCreator();
        }

        @Override
        public ExplainPlanLogger getExplainPlanLogger() {
            return new MySQLExplainPlanLogger();
         }
    };

    private static final Map<String, DatabaseDialect> DBNAMEMAP = new HashMap<>(values().length, 1);;

    static {
        for (DatabaseDialect db : values())
            DBNAMEMAP.put(db.dbName, db);
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

    public boolean isSupported() {
        return supported;
    }

    public static List<DatabaseDialect> getSupportedDatabases() {
        return Stream.of(DatabaseDialect.values())
                .filter(d -> d.isSupported())
                .collect(Collectors.toList());
    }

    public ExplainPlanQueryCreator getExplainPlanQueryCreator() {
        throw new UnsupportedOperationException(
                "{} is not currently supported and doesn't have an explain plan query creator");
    }

    public ExplainPlanLogger getExplainPlanLogger() {
       return new DefaultExplainPlanLogger();
    }

    public static Optional<DatabaseDialect> getDatabaseDialectByURL(String jdbcURL) {
        DatabaseDialect dbDialect = null;
        if (jdbcURL != null) {
            String[] splitStr = jdbcURL.split(":");
            if (splitStr != null && splitStr.length >= 2) {
                dbDialect = DBNAMEMAP.get(splitStr[1].toLowerCase());
            }
        }
        return Optional.ofNullable(dbDialect);
    }

    @Override
    public String toString() {
        return dbName;
    }

}
