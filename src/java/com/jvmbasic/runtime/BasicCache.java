package com.jvmbasic.runtime;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import net.spy.memcached.MemcachedClient;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * BasicCache - Redis and Memcached client for JVM BASIC 2.0
 *
 * Provides caching support via two namespaces:
 * - Redis: Full Redis client using Jedis (GET, SET, DEL, EXPIRE, TTL, INCR, DECR, hash, list, set operations)
 * - Memcached: Memcached client using Spymemcached
 *
 * Usage in BASIC:
 *   var conn as Integer = Redis.Connect("localhost", 6379)
 *   Redis.Set(conn, "key", "value")
 *   var val as String = Redis.Get(conn, "key")
 *   Redis.Close(conn)
 */
public class BasicCache {

    // Connection storage
    private static Map<Integer, Jedis> redisConnections = new ConcurrentHashMap<>();
    private static Map<Integer, MemcachedClient> memcachedConnections = new ConcurrentHashMap<>();
    private static int nextConnectionId = 1;

    // ========================================================================
    // Redis namespace methods (using Jedis)
    // ========================================================================

    /**
     * Connect to a Redis server.
     * @param host Redis host
     * @param port Redis port (usually 6379)
     * @return Connection ID (positive) on success, -1 on error
     */
    public static int redis_Connect(String host, int port) {
        try {
            Jedis jedis = new Jedis(host, port);
            jedis.ping(); // Test connection
            int id = nextConnectionId++;
            redisConnections.put(id, jedis);
            return id;
        } catch (Exception e) {
            System.err.println("Redis.Connect error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Connect to Redis with password authentication.
     * @param host Redis host
     * @param port Redis port
     * @param password Redis password
     * @return Connection ID on success, -1 on error
     */
    public static int redis_ConnectAuth(String host, int port, String password) {
        try {
            Jedis jedis = new Jedis(host, port);
            jedis.auth(password);
            jedis.ping(); // Test connection
            int id = nextConnectionId++;
            redisConnections.put(id, jedis);
            return id;
        } catch (Exception e) {
            System.err.println("Redis.ConnectAuth error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Close a Redis connection.
     * @param connId Connection ID
     */
    public static void redis_Close(int connId) {
        Jedis jedis = redisConnections.remove(connId);
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    /**
     * Set a key-value pair.
     * @param connId Connection ID
     * @param key Key
     * @param value Value
     * @return 0 on success, -1 on error
     */
    public static int redis_Set(int connId, String key, String value) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return -1;
        try {
            jedis.set(key, value);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Set a key-value pair with expiration.
     * @param connId Connection ID
     * @param key Key
     * @param value Value
     * @param seconds Expiration time in seconds
     * @return 0 on success, -1 on error
     */
    public static int redis_SetEx(int connId, String key, String value, int seconds) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return -1;
        try {
            jedis.setex(key, seconds, value);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Get a value by key.
     * @param connId Connection ID
     * @param key Key
     * @return Value or empty string if not found
     */
    public static String redis_Get(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "";
        try {
            String value = jedis.get(key);
            return value != null ? value : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Delete a key.
     * @param connId Connection ID
     * @param key Key
     * @return Number of keys deleted
     */
    public static long redis_Del(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.del(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if a key exists.
     * @param connId Connection ID
     * @param key Key
     * @return 1 if exists, 0 if not
     */
    public static int redis_Exists(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.exists(key) ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Set expiration on a key.
     * @param connId Connection ID
     * @param key Key
     * @param seconds Expiration time in seconds
     * @return 1 if timeout was set, 0 if key doesn't exist
     */
    public static long redis_Expire(int connId, String key, int seconds) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get time-to-live for a key.
     * @param connId Connection ID
     * @param key Key
     * @return TTL in seconds, -1 if no expiry, -2 if key doesn't exist
     */
    public static long redis_TTL(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return -2;
        try {
            return jedis.ttl(key);
        } catch (Exception e) {
            return -2;
        }
    }

    /**
     * Increment a key's value.
     * @param connId Connection ID
     * @param key Key
     * @return New value after increment
     */
    public static long redis_Incr(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.incr(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Increment a key's value by amount.
     * @param connId Connection ID
     * @param key Key
     * @param amount Amount to increment
     * @return New value after increment
     */
    public static long redis_IncrBy(int connId, String key, long amount) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.incrBy(key, amount);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Decrement a key's value.
     * @param connId Connection ID
     * @param key Key
     * @return New value after decrement
     */
    public static long redis_Decr(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.decr(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Decrement a key's value by amount.
     * @param connId Connection ID
     * @param key Key
     * @param amount Amount to decrement
     * @return New value after decrement
     */
    public static long redis_DecrBy(int connId, String key, long amount) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.decrBy(key, amount);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Set a hash field.
     * @param connId Connection ID
     * @param key Hash key
     * @param field Field name
     * @param value Field value
     * @return 1 if new field, 0 if updated existing
     */
    public static long redis_HSet(int connId, String key, String field, String value) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get a hash field.
     * @param connId Connection ID
     * @param key Hash key
     * @param field Field name
     * @return Field value or empty string
     */
    public static String redis_HGet(int connId, String key, String field) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "";
        try {
            String value = jedis.hget(key, field);
            return value != null ? value : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Delete a hash field.
     * @param connId Connection ID
     * @param key Hash key
     * @param field Field name
     * @return 1 if deleted, 0 if not found
     */
    public static long redis_HDel(int connId, String key, String field) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.hdel(key, field);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get all fields and values in a hash as JSON.
     * @param connId Connection ID
     * @param key Hash key
     * @return JSON string of hash contents
     */
    public static String redis_HGetAll(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "{}";
        try {
            Map<String, String> hash = jedis.hgetAll(key);
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, String> entry : hash.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                  .append(escapeJson(entry.getValue())).append("\"");
                first = false;
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Push value to left of list.
     * @param connId Connection ID
     * @param key List key
     * @param value Value to push
     * @return New length of list
     */
    public static long redis_LPush(int connId, String key, String value) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Push value to right of list.
     * @param connId Connection ID
     * @param key List key
     * @param value Value to push
     * @return New length of list
     */
    public static long redis_RPush(int connId, String key, String value) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.rpush(key, value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Pop value from left of list.
     * @param connId Connection ID
     * @param key List key
     * @return Popped value or empty string
     */
    public static String redis_LPop(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "";
        try {
            String value = jedis.lpop(key);
            return value != null ? value : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Pop value from right of list.
     * @param connId Connection ID
     * @param key List key
     * @return Popped value or empty string
     */
    public static String redis_RPop(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "";
        try {
            String value = jedis.rpop(key);
            return value != null ? value : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get length of list.
     * @param connId Connection ID
     * @param key List key
     * @return List length
     */
    public static long redis_LLen(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.llen(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get range of list elements as JSON array.
     * @param connId Connection ID
     * @param key List key
     * @param start Start index
     * @param stop Stop index (-1 for end)
     * @return JSON array of values
     */
    public static String redis_LRange(int connId, String key, long start, long stop) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "[]";
        try {
            List<String> values = jedis.lrange(key, start, stop);
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (String value : values) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(value)).append("\"");
                first = false;
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Add member to set.
     * @param connId Connection ID
     * @param key Set key
     * @param member Member to add
     * @return 1 if added, 0 if already existed
     */
    public static long redis_SAdd(int connId, String key, String member) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.sadd(key, member);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if member exists in set.
     * @param connId Connection ID
     * @param key Set key
     * @param member Member to check
     * @return 1 if member, 0 if not
     */
    public static int redis_SIsMember(int connId, String key, String member) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.sismember(key, member) ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Remove member from set.
     * @param connId Connection ID
     * @param key Set key
     * @param member Member to remove
     * @return 1 if removed, 0 if not found
     */
    public static long redis_SRem(int connId, String key, String member) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return 0;
        try {
            return jedis.srem(key, member);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get all members of set as JSON array.
     * @param connId Connection ID
     * @param key Set key
     * @return JSON array of members
     */
    public static String redis_SMembers(int connId, String key) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "[]";
        try {
            Set<String> members = jedis.smembers(key);
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (String member : members) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(member)).append("\"");
                first = false;
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Ping the server.
     * @param connId Connection ID
     * @return "PONG" if successful, empty string on error
     */
    public static String redis_Ping(int connId) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return "";
        try {
            return jedis.ping();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Flush all keys in current database.
     * @param connId Connection ID
     * @return 0 on success, -1 on error
     */
    public static int redis_FlushDB(int connId) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return -1;
        try {
            jedis.flushDB();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Select a Redis database.
     * @param connId Connection ID
     * @param index Database index (0-15 typically)
     * @return 0 on success, -1 on error
     */
    public static int redis_Select(int connId, int index) {
        Jedis jedis = redisConnections.get(connId);
        if (jedis == null) return -1;
        try {
            jedis.select(index);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    // ========================================================================
    // Memcached namespace methods (using Spymemcached)
    // ========================================================================

    /**
     * Connect to a Memcached server.
     * @param host Memcached host
     * @param port Memcached port (usually 11211)
     * @return Connection ID on success, -1 on error
     */
    public static int memcached_Connect(String host, int port) {
        try {
            MemcachedClient client = new MemcachedClient(
                new InetSocketAddress(host, port));
            int id = nextConnectionId++;
            memcachedConnections.put(id, client);
            return id;
        } catch (Exception e) {
            System.err.println("Memcached.Connect error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Close a Memcached connection.
     * @param connId Connection ID
     */
    public static void memcached_Close(int connId) {
        MemcachedClient client = memcachedConnections.remove(connId);
        if (client != null) {
            try {
                client.shutdown();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    /**
     * Set a key-value pair with expiration.
     * @param connId Connection ID
     * @param key Key
     * @param value Value
     * @param expiry Expiration time in seconds (0 = no expiry)
     * @return 0 on success, -1 on error
     */
    public static int memcached_Set(int connId, String key, String value, int expiry) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            client.set(key, expiry, value).get();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Get a value by key.
     * @param connId Connection ID
     * @param key Key
     * @return Value or empty string if not found
     */
    public static String memcached_Get(int connId, String key) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return "";
        try {
            Object value = client.get(key);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Delete a key.
     * @param connId Connection ID
     * @param key Key
     * @return 0 on success, -1 on error
     */
    public static int memcached_Delete(int connId, String key) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            client.delete(key).get();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Increment a key's value.
     * @param connId Connection ID
     * @param key Key
     * @param amount Amount to increment
     * @return New value, or -1 on error
     */
    public static long memcached_Incr(int connId, String key, long amount) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            return client.incr(key, amount);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Decrement a key's value.
     * @param connId Connection ID
     * @param key Key
     * @param amount Amount to decrement
     * @return New value, or -1 on error
     */
    public static long memcached_Decr(int connId, String key, long amount) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            return client.decr(key, amount);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Add a value (only if key doesn't exist).
     * @param connId Connection ID
     * @param key Key
     * @param value Value
     * @param expiry Expiration in seconds
     * @return 0 on success, -1 on error or key exists
     */
    public static int memcached_Add(int connId, String key, String value, int expiry) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            boolean result = client.add(key, expiry, value).get();
            return result ? 0 : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Replace a value (only if key exists).
     * @param connId Connection ID
     * @param key Key
     * @param value Value
     * @param expiry Expiration in seconds
     * @return 0 on success, -1 on error or key doesn't exist
     */
    public static int memcached_Replace(int connId, String key, String value, int expiry) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            boolean result = client.replace(key, expiry, value).get();
            return result ? 0 : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Append to existing value.
     * @param connId Connection ID
     * @param key Key
     * @param value Value to append
     * @return 0 on success, -1 on error
     */
    public static int memcached_Append(int connId, String key, String value) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            // CAS ID 0 means we don't care about version
            boolean result = client.append(key, value).get();
            return result ? 0 : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Flush all keys.
     * @param connId Connection ID
     * @return 0 on success, -1 on error
     */
    public static int memcached_Flush(int connId) {
        MemcachedClient client = memcachedConnections.get(connId);
        if (client == null) return -1;
        try {
            client.flush().get();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
