package cedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RedisOperator {
    public static int PAGE_SIZE = 100;
    private Map<FavoriteConnection.Connection, JedisPool> jedisPoolMap = new HashMap<>();
    private Map<FavoriteConnection.Connection, String> cursorMap = new HashMap<>();

    public Map<FavoriteConnection.Connection, JedisPool> getJedisPoolMap() {
        return jedisPoolMap;
    }

    public Map<FavoriteConnection.Connection, String> getCursorMap() {
        return cursorMap;
    }

    public synchronized JedisPool getJedisPool(FavoriteConnection.Connection connection) {
        JedisPool jedisPool = null;
        for (Map.Entry<FavoriteConnection.Connection, JedisPool> entry : jedisPoolMap.entrySet()) {
            FavoriteConnection.Connection key = entry.getKey();
            if (key.equals(connection)) {
                if (key.getHost().equals(connection.getHost())
                        && key.getPort() == connection.getPort()
                        && (key.getPassword() == null || key.getPassword().equals(connection.getPassword()))) {
                    jedisPool = entry.getValue();
                    break;
                }
            }
        }
        if (jedisPool == null) {
            jedisPool = new JedisPool(new GenericObjectPoolConfig(), connection.getHost(),
                    connection.getPort(), Protocol.DEFAULT_TIMEOUT * 2, connection.getPassword());
            if (jedisPoolMap.get(connection) != null)
                jedisPoolMap.get(connection).close();
            jedisPoolMap.put(connection, jedisPool);
            cursorMap.put(connection, "0");
        }
        return jedisPool;
    }

    public synchronized List<Key> getDbContent(int db, FavoriteConnection.Connection connection, boolean startZero) {
        JedisPool jedisPool = getJedisPool(connection);
        if (startZero)
            cursorMap.put(connection, "0");
        String cursor = cursorMap.get(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            if (cursor.equals("-1")) {
                cursorMap.put(connection, "0");
                return null;
            }
            ScanParams scanParams = new ScanParams();
            ScanResult<String> result = jedis.scan(cursor, scanParams.count(PAGE_SIZE));
            if (result.getStringCursor().equals("0"))
                cursorMap.put(connection, "-1");
            else
                cursorMap.put(connection, result.getStringCursor());
            List<Key> res = convert(result.getResult(), jedis);
            return res;
        }
    }

    public synchronized List<String> getDbSubContent(int db, FavoriteConnection.Connection connection, Key key) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            List<String> search = search(null, key, connection, db);
            //search.sort(String::compareTo);
            return search;
        }
    }

    public long getDbSize(int db, FavoriteConnection.Connection connection) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            return jedis.dbSize();
        }
    }

    public int getDbCount(FavoriteConnection.Connection connection) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> list = jedis.configGet("databases");
            return Integer.valueOf(list.get(1));
        }
    }

    public List<Key> search(String pattern, FavoriteConnection.Connection connection, int db) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            return convert(jedis.keys(pattern), jedis);
        }
    }

    public List<String> search(String pattern, Key key, FavoriteConnection.Connection connection, int db) {
        Pattern[] pt = {null};
        if (pattern != null && pattern.length() > 0) {
            pt[0] = Pattern.compile(pattern);
        }
        JedisPool jedisPool = getJedisPool(connection);
        Collection<String> subs = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            switch (key.getType()) {
                case SET:
                    subs = jedis.smembers(key.getName());
                    break;
                case HASH:
                    subs = jedis.hkeys(key.getName());
                    break;
                case ZSET:
                    subs = jedis.zrange(key.getName(), 0, -1);
                    break;
                case LIST:
                    subs = jedis.lrange(key.getName(), 0, -1);
                    break;
            }
        }
        return subs.stream().filter(s -> {
            if (pt[0] != null) {
                Matcher matcher = pt[0].matcher(s);
                return matcher.matches();
            } else {
                return true;
            }
        }).collect(Collectors.toList());
    }


    private List<Key> convert(Collection<String> rowKeys, Jedis jedis) {
        List<Key> keys = new ArrayList<>();
        for (String k : rowKeys) {
            String type = jedis.type(k);
            Key key = null;
            switch (type) {
                case "string":
                    key = new Key(k, Key.Type.STRING);
                    break;
                case "set":
                    key = new Key(k, Key.Type.SET);
                    break;
                case "list":
                    key = new Key(k, Key.Type.LIST);
                    break;
                case "zset":
                    key = new Key(k, Key.Type.ZSET);
                    break;
                case "hash":
                    key = new Key(k, Key.Type.HASH);
                    break;
                default:
                    break;
            }
            if (key != null) {
                keys.add(key);
            }
        }
        return keys;
    }

    public int add(String type, FavoriteConnection.Connection connection, int db, String key, String filed, String value) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            boolean exists = jedis.exists(key);
            if (exists)
                return 0; //键已存在
            switch (type) {
                case "STRING":
                    jedis.set(key, value);
                    break;
                case "SET":
                    jedis.sadd(key, value);
                    break;
                case "ZSET":
                    jedis.zadd(key, Double.valueOf(filed), value);
                    break;
                case "HASH":
                    jedis.hset(key, filed, value);
                    break;
                case "LIST":
                    jedis.lpush(key, value);
                    break;
            }
        }
        return 1;
    }

    public int addField(String type, FavoriteConnection.Connection connection, int db, String key, String filed, String value, Integer position) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            switch (type) {
                case "SET":
                    jedis.sadd(key, value);
                    break;
                case "ZSET":
                    jedis.zadd(key, Double.valueOf(value), filed);
                    break;
                case "HASH":
                    if (jedis.hexists(key, filed)) {
                        return 0;
                    }
                    jedis.hset(key, filed, value);
                    break;
                case "LIST":
                    if (position == 1) {//头
                        jedis.lpush(key, value);
                    } else {//尾
                        jedis.rpush(key, value);
                    }
                    break;
            }
        }
        return 1;
    }

    public void delete(FavoriteConnection.Connection connection, int db, Key key) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            jedis.del(key.getName());
        }
    }

    public void delete(FavoriteConnection.Connection connection, int db, Key key, String field) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            switch (key.getType()) {
                case HASH:
                    jedis.hdel(key.getName(), field);
                    break;
                case LIST:
                    jedis.lrem(key.getName(), 1, field);
                    break;
                case SET:
                    jedis.srem(key.getName(), field);
                    break;
                case ZSET:
                    jedis.zrem(key.getName(), field);
                    break;
            }
        }
    }

    public int rename(Key key, String newName, FavoriteConnection.Connection connection, int db) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            Boolean exists = jedis.exists(newName);
            if (exists)
                return 0;//键已存在
            jedis.rename(key.getName(), newName);
        }
        return 1;
    }

    public int rename(Key key, String newField, String oldField, FavoriteConnection.Connection connection, int db) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            switch (key.getType()) {
                case HASH:
                    Boolean exist = jedis.hexists(key.getName(), newField);
                    if (exist)
                        return 0;
                    String value = jedis.hget(key.getName(), oldField);
                    jedis.hset(key.getName(), newField, value);
                    jedis.hdel(key.getName(), oldField);
                    break;
            }
        }
        return 1;
    }

    public String get(FavoriteConnection.Connection connection, int db, Key key) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            return jedis.get(key.getName());
        }
    }

    public void save(FavoriteConnection.Connection connection, int db, Key key, String value) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            jedis.set(key.getName(), value);
        }
    }

    public void save(FavoriteConnection.Connection connection, int db, Key key, String field, String value) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            switch (key.getType()) {
                case HASH:
                    jedis.hset(key.getName(), field, value);
                    break;
                case LIST:
                    jedis.lset(key.getName(), Long.valueOf(field), value);
                    break;
                case SET:
                    jedis.srem(key.getName(), field);
                    jedis.sadd(key.getName(), value);
                    break;
                case ZSET:
                    Double score = jedis.zscore(key.getName(), field);
                    jedis.zrem(key.getName(), field);
                    jedis.zadd(key.getName(), score, value);
                    break;
            }
        }
    }

    public String getValue(FavoriteConnection.Connection connection, int db, Key key, String field) {
        JedisPool jedisPool = getJedisPool(connection);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(db);
            switch (key.getType()) {
                case HASH:
                    return jedis.hget(key.getName(), field);
                default:
                    return null;
            }
        }
    }
}

