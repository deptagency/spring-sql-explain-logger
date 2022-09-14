package com.deptagency.sqlexplain.listener;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SQLQueriesCache {

    private final int maxSize;
    private final int expiryPeriod;
    private ConcurrentHashMap<String, CachedQuery> map;
    private ConcurrentLinkedQueue<String> queue;
    
    public SQLQueriesCache(final int maxSize, final int expiryPeriod) {
        this.maxSize = maxSize;
        this.expiryPeriod = expiryPeriod;
        map = new ConcurrentHashMap<String, CachedQuery>(maxSize);
        queue = new ConcurrentLinkedQueue<String>();
    }
    

    /**
     * Add a query to the cahe if it does not exist or if it exists but has passed its expiry period
     * @param query
     * @return true if the query was added to the cache
     *         false if the query already exists and it has not reached its expiry period
     */
    public Boolean addQuery(final String query) {
        CachedQuery cachedQuery = map.get(query);
        Boolean added = false;
        if (cachedQuery == null) {
            added = true;
            put(query);
        } else if (Duration.between(cachedQuery.getTime(), Instant.now()).toMinutes() > expiryPeriod) {
            added = true;
            queue.remove(query);
            put(query);
        }
        return added;
    }


    /**
     * @param query
     */
    private void put(final String query) {
        while (queue.size() >= maxSize) {
            String oldestQuery = queue.poll();
            if (oldestQuery != null) {
                map.remove(oldestQuery);
            }
        }
        queue.add(query);
        map.put(query, new CachedQuery(query, Instant.now()));
    }
    
    /**
     * @param query - the query to get from cache
     * @return the value associated to the given query or null
     */
    public Optional<CachedQuery> get(final String query) {
        return Optional.ofNullable(map.get(query));
    }

    class CachedQuery {
        private String query;
        private Instant time;
        public CachedQuery(String query, Instant time) {
            this.query = query;
            this.time = time;
        }
        public String getQuery() {
            return query;
        }
        public void setQuery(String query) {
            this.query = query;
        }
        public Instant getTime() {
            return time;
        }
        public void setTime(Instant time) {
            this.time = time;
        }
        
    }
}
