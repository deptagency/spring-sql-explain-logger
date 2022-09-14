package com.deptagency.sqlexplain.listener;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SQLCacheQueriesTest {
    

    @Test
	void shouldAddNewQueryToCache() {

        SQLQueriesCache cache = new SQLQueriesCache(10, 5);

        assertTrue( cache.addQuery("new query"), "Should add query to cache");
	}

    @Test
	void shouldNotAddExistingQueryIfNotExpired() {

        SQLQueriesCache cache = new SQLQueriesCache(10, 5);
        
        String query = "existing query";

        cache.addQuery(query);

        assertFalse( cache.addQuery(query), "Should not add a query if it has not reached expiry preiod");
	}

    //TODO add expiry period test

}
