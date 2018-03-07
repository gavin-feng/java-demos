package com.wnbt.base.store.test;

import com.alibaba.fastjson.JSON;
import com.wnbt.base.store.CacheStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheStoreTest {
    private static Logger logger = LoggerFactory.getLogger(CacheStoreTest.class);
    @Autowired
    private CacheStore cacheStore;

    @Test
    public void testRpush() {
        String listKey = "listkey-java-demo";
        cacheStore.del(listKey);
        SimpleObject a1 = new SimpleObject();
        a1.setX(2);
        a1.setY(55);
        cacheStore.rpush(listKey, a1, CacheStore.TWO_WEEKS);

        List<SimpleObject> objects = cacheStore.lrange(listKey, 0, -1, SimpleObject.class);
        logger.info(JSON.toJSONString(objects));
    }

    @Test
    public void testRpushList() {
        String listKey = "listkey-java-demo";
        cacheStore.del(listKey);

        List<SimpleObject> simpleObjs = new ArrayList<>();

        SimpleObject a1 = new SimpleObject();
        a1.setX(2);
        a1.setY(55);
        simpleObjs.add(a1);
        SimpleObject a2 = new SimpleObject();
        a2.setX(21);
        a2.setY(5);
        simpleObjs.add(a2);

        cacheStore.rpushList(listKey, simpleObjs, CacheStore.ONE_HOUR);

        List<SimpleObject> objects = cacheStore.lrange(listKey, 0, -1, SimpleObject.class);
        logger.info(JSON.toJSONString(objects));
    }
}

class SimpleObject {
    int x;
    int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
