package com.deptagency.sqlexplain.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class DatabaseDialectTest {
    

    @Test
	void shouldThrowExceptionForUnsupportedDB() {

        DatabaseDialect db = DatabaseDialect.ORACLE;

        assertThrows( UnsupportedOperationException.class, () -> {
            db.getExplainPlanQueryCreator();
        }, "Should throw unsupported operation");
	}
    
    @Test
	void shouldReturnProgreSQLDialectFromURL() {

        Optional<DatabaseDialect> db = DatabaseDialect.getDatabaseDialectByURL("jdbc:postgresql://localhost");

        assertEquals(DatabaseDialect.POSTGRESQL, db.get(), "Should return PostgreSQL");
	}

    @Test
	void shouldreturnMySQLDialectFromURL() {

        Optional<DatabaseDialect> db = DatabaseDialect.getDatabaseDialectByURL("jdbc:mysql://localhost");

        assertEquals(DatabaseDialect.MYSQL, db.get(), "Should return MySQL");
	}

    @Test
	void shouldreturnOracleDialectFromURL() {

        Optional<DatabaseDialect> db = DatabaseDialect.getDatabaseDialectByURL("jdbc:oracle://localhost");

        assertEquals(DatabaseDialect.ORACLE, db.get(), "Should return Oracle");
	}

    @Test
	void shouldreturnNullForNoDialect() {

        Optional<DatabaseDialect> db = DatabaseDialect.getDatabaseDialectByURL("jdbc:xxx://localhost");

        assertFalse(db.isPresent(), "Should return empty");
	}
}
