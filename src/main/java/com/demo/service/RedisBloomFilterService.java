package com.demo.service;

import com.demo.utils.BloomFilterHelper;

import java.util.List;

/**
 * @author Reece Lin
 * @version 1.00
 * @time 2020/8/15 19:44
 */
public interface RedisBloomFilterService {

    void delete(String key);

    <T> void add(BloomFilterHelper<T> bloomFilterHelper, String key, T value);

    <T> void addList(BloomFilterHelper<T> bloomFilterHelper, String key, List<T> valueList);

    <T> boolean contains(BloomFilterHelper<T> bloomFilterHelper, String key, T value);
}
