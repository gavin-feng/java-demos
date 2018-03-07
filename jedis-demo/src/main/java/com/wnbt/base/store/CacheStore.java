package com.wnbt.base.store;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CacheStore {
    private static Logger logger = LoggerFactory.getLogger(CacheStore.class);

    public static final int ONE_MINUTE = 60;            // 1 分钟
    public static final int SIX_MINUTE = 6 * 60;          // 6 分钟
    public static final int ONE_HOUR = 60 * 60;           // 1 小时
    public static final int ONE_DAY = 24 * 60 * 60;         // 1 天
    public static final int TWO_WEEKS = 2 * 7 * 24 * 60 * 60;   // 2 周
    public static final int HALF_YEAR = 180 * 24 * 60 * 60;   // 180 天

    public static final TypeReference<Map<String, List<String>>> ListString_Map_Type_Ref
            = new TypeReference<Map<String, List<String>>>() {
    };

    @Autowired
    private JedisPool jedisPool;

    public void set(String key, Object value, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            String valueJson = JSON.toJSONString(value);
            jedis.set(key, valueJson);
            jedis.expire(key, timeout);
        }
    }

    public void hset(String key, String field, Object value, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            String valueJson = JSON.toJSONString(value);
            jedis.hset(key, field, valueJson);
            jedis.expire(key, timeout);
        }
    }

    /**
     * 复杂的集合对象时，使用该方法，比如 需要返回 Map<String, StockDealDO> 对象
     *
     * @param key
     * @param type，比如 new TypeReference<Map<String, StockDealDO>>(){}
     * @param <T>
     * @return
     */
    public <T> T getObject(String key, TypeReference<T> type) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            return JSON.parseObject(json, type);
        }
    }

    public <T> List<T> getArray(String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            return JSON.parseArray(json, clazz);
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            return JSON.parseObject(json, clazz);
        }
    }

    public String getString(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public <T> List<T> hgetArray(String key, String field, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.hget(key, field);
            return JSON.parseArray(json, clazz);
        }
    }

    public <T> T hgetObject(String key, String field, Class<T> clazz) {
        logger.info(key+"\t"+field+"\t----1----");
        try (Jedis jedis = jedisPool.getResource()) {
            logger.info(key+"\t"+field+"\t----2----");
            String json = jedis.hget(key, field);
            logger.info(key+"\t"+field+"\t----json----"+json);
            return JSON.parseObject(json, clazz);
        }
    }

    public Map<String, String> hgetAll(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    public Long hdel(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, field);
        }
    }

    public Long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }

    public void rpush(String key, Object value, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            String valueJson = JSON.toJSONString(value);
            jedis.rpush(key, valueJson);
            jedis.expire(key, timeout);
        }
    }


    public <T> void rpushList(String key, List<T> objs, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (objs == null) {
                return;
            }
            String[] objArray = new String[objs.size()];
            int i = 0;
            for (T obj : objs) {
                String valueJson = JSON.toJSONString(obj);
                objArray[i++] = valueJson;
            }
            jedis.rpush(key, objArray);
            jedis.expire(key, timeout);
        }
    }

    public <T> T rpop(String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.rpop(key);
            return JSON.parseObject(json, clazz);
        }
    }

    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> jsonValues = jedis.lrange(key, start, end);
            List<T> res = new ArrayList<>();
            for (String jsonValue : jsonValues) {
                T obj = JSON.parseObject(jsonValue, clazz);
                res.add(obj);
            }
            return res;
        }
    }

    public <T> void sadd(String key, List<T> objs, int timeout){
        try (Jedis jedis = jedisPool.getResource()) {
            if (objs == null) {
                return;
            }
            String[] objArray = new String[objs.size()];
            int i = 0;
            for (T obj : objs) {
                String valueJson = JSON.toJSONString(obj);
                objArray[i++] = valueJson;
            }
            jedis.sadd(key, objArray);
            jedis.expire(key, timeout);
        }
    }

    // 程序关闭的时候销毁
    @PreDestroy
    public void destroy() {
        logger.info("CacheStore 销毁！");
        jedisPool.destroy();
    }
}
