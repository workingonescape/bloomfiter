package com.demo.service.impl;

import com.google.common.base.Preconditions;
import com.demo.service.RedisBloomFilterService;
import com.demo.utils.BloomFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Reece Lin
 * @version 1.00
 * @time 2020/8/15 19:40
 */
@Service
public class RedisBloomFilterServiceImpl<T> implements RedisBloomFilterService {



    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 删除缓存的KEY
     *
     * @param key KEY
     */
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 根据给定的布隆过滤器添加值，在添加一个元素的时候使用，批量添加的性能差
     *
     * @param bloomFilterHelper 布隆过滤器对象
     * @param key               KEY
     * @param value             值
     * @param <T>               泛型，可以传入任何类型的value
     */
    @Override
    public <T> void add(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            redisTemplate.opsForValue().setBit(key, i, true);
        }
    }


    /**
     * 根据给定的布隆过滤器添加值，在添加一批元素的时候使用，批量添加的性能好，使用pipeline方式(如果是集群下，请使用优化后RedisPipeline的操作)
     *
     * @param bloomFilterHelper 布隆过滤器对象
     * @param key               KEY
     * @param valueList         值，列表
     * @param <T>               泛型，可以传入任何类型的value
     */
    @Override
    public <T> void addList(BloomFilterHelper<T> bloomFilterHelper, String key, List<T> valueList) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        redisTemplate.executePipelined((RedisCallback<Long>) connection -> {
            connection.openPipeline();
            for (T value : valueList) {
                int[] offset = bloomFilterHelper.murmurHashOffset(value);
                for (int i : offset) {
                    connection.setBit(key.getBytes(), i, true);
                }
            }
            return null;
        });
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     *
     * @param bloomFilterHelper 布隆过滤器对象
     * @param key               KEY
     * @param value             值
     * @param <T>               泛型，可以传入任何类型的value
     * @return 是否存在
     */
    @Override
    public <T> boolean contains(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }
        return true;
    }
}
