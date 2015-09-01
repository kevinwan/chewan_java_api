package com.gongpingjia.carplay.cache.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.gongpingjia.carplay.cache.CacheService;

/**
 * Cache服务的实现类，对缓存进行操作
 * 
 * @author licheng
 *
 */
@Service
public class CacheServiceImpl implements CacheService {

	private static final Logger LOG = LoggerFactory.getLogger(CacheServiceImpl.class);

	@Autowired
	private ShardedJedisPool shardedJedisPool;

	@Override
	public String get(String key) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.get(key);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> type) {

		String result = get(key);

		if (result == null) {
			// 即找不到缓存对象
			return null;
		}

		JSONObject json = JSONObject.fromObject(result);

		return (T) JSONObject.toBean(json, type);
	}

	@Override
	public String set(String key, String value) {

		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.set(key, value);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

	@Override
	public <T> String set(String key, T value) {
		JSONObject json = JSONObject.fromObject(value);

		return set(key, json.toString());
	}

	@Override
	public Long del(String key) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.del(key);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

	@Override
	public String hget(String key, String field) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T hget(String key, String field, Class<?> type) {
		String result = hget(key, field);

		JSONObject json = JSONObject.fromObject(result);

		return (T) JSONObject.toBean(json, type);
	}

	@Override
	public Long hset(String key, String field, String value) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

	@Override
	public <T> Long hset(String key, String field, T value) {

		JSONObject json = JSONObject.fromObject(value);

		return hset(key, field, json.toString());
	}

	@Override
	public Boolean exists(String key) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.exists(key);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return Boolean.FALSE;
	}

	@Override
	public Boolean hexists(String key, String field) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.hexists(key, field);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return Boolean.FALSE;
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.hmget(key, fields);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return new ArrayList<String>(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> hmget(String key, Class<?> type, String... fields) {
		List<String> reslut = hmget(key, fields);

		List<T> list = new ArrayList<T>(reslut.size());
		for (String jsonStr : reslut) {
			JSONObject json = JSONObject.fromObject(jsonStr);
			list.add((T) JSONObject.toBean(json, type));
		}

		return list;
	}

	@Override
	public Long hdel(String key, String field) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.hdel(key, field);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

	@Override
	public Long expire(String key, int seconds) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			LOG.error("Execute redis command failure", e.getMessage());
		} finally {
			jedis.close();
		}
		return null;
	}

}
