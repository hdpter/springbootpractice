package com.springbootpractice.demo.redis;

import com.springbootpractice.demo.redis.param.OrderVo;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoRedisApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void stringRedisTest() {
        final ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations
                .set("key1", "value1", Duration.ofMinutes(1));

        final Object value = valueOperations.get("key1");

        Assert.isTrue(Objects.equals("value1", value), "set失败");

        final HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("hash1", "f1", "v1");
        hashOperations.put("hash1", "f2", "v2");

        hashOperations.values("hash1").forEach(System.out::println);
    }


    @Test
    void redisCallbackTest() {
        redisTemplate.execute((RedisCallback) connection -> {
            connection.set("rkey1".getBytes(), "rv1".getBytes());
            connection.set("rkey2".getBytes(), "rv2".getBytes());
            return null;
        });
    }

    @Test
    void sessionCallbackTest() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                final ListOperations listOperations = operations.opsForList();
                listOperations.leftPush("sk1", "sv1");
                listOperations.leftPush("sk1", "sv2");
                listOperations.getOperations().expire("sk1", 1, TimeUnit.MINUTES);

                listOperations.range("sk1", 0, 2).forEach(System.out::println);
                return 1;
            }
        });
    }

    @Test
    void stringTest() {
        redisTemplate.opsForValue().set("stringKey1", "value1", 5, TimeUnit.MINUTES);

        //字符串类型的整数，不能进行数字运算；
        redisTemplate.opsForValue().set("stringKey2", "1", 5, TimeUnit.MINUTES);

        //进行数字运算，增加，减少
        redisTemplate.opsForValue().set("stringKey3", 1, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().increment("stringKey3",2);
        redisTemplate.opsForValue().decrement("stringKey3",1);

        //其它操作方法
        final Long keySize = redisTemplate.opsForValue().size("stringKey1");
        System.out.println(keySize);

        //批量设置
        Map<String,Long> map = new HashMap<>(4);
        map.put("sk1",1L);
        map.put("sk2",2L);
        map.put("sk3",3L);
        map.put("sk4",4L);
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.opsForValue().multiSetIfAbsent(map);
        //批量获取
        redisTemplate.opsForValue().multiGet(map.keySet()).forEach(System.out::println);


        //getAndSet
        final Object sk5Value = redisTemplate.opsForValue().getAndSet("sk5", 100);
        System.out.println("sk5Value:"+sk5Value);

        redisTemplate.opsForValue().append("sk5","hello redis");
        System.out.println("sk5Value2:"+redisTemplate.opsForValue().get("sk5"));

        //按照情况设置，可以省去了之前查询出来之后判断是否存在再操作的代码；
        redisTemplate.opsForValue().setIfAbsent("sk6",1000,5,TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfPresent("sk6",100,5,TimeUnit.MINUTES);

        redisTemplate.opsForValue().set("order",
                OrderVo.builder().id(1L).title("iphone11").price(new BigDecimal(5555)).created(LocalDateTime.now()).build(),
                Duration.ofMinutes(5)
        );

        OrderVo orderVo = (OrderVo) redisTemplate.opsForValue().get("order");
        System.out.println("order:" + orderVo);

        Assert.notNull(orderVo,"获取订单对象失败");

    }
}
