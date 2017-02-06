package com.glitter.demo.redis.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class PipelineDemo {

	private static final Jedis jedis;

	static {
		jedis = new Jedis("127.0.0.1", 6379);
		jedis.auth("limj");
	}

	/**
	 * 不使用Pipeline操作的情况
	 * 
	 * @param count
	 */
	public void withoutPipeline(int count) {
		long start = System.currentTimeMillis();
		try {
			for (int i = 0; i < count; i++) {
				jedis.incr("testKey1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.disconnect();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("withoutPipeline: " + (end - start));
	}

	/**
	 * 使用Pipeline操作的情况
	 * 
	 * @param count
	 */
	public void usePipeline(int count) {
		long start = System.currentTimeMillis();
		try {
			Pipeline pl = jedis.pipelined();
			for (int i = 0; i < count; i++) {
				pl.incr("testKey2");
			}
			pl.sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.disconnect();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("usePipeline: " + (end - start));
	}

	public void demo() {
		Map<String, String> data = new HashMap<String, String>();
		jedis.select(8);
		jedis.flushDB();
		// hmset
		long start = System.currentTimeMillis();
		// 直接hmset
		for (int i = 0; i < 10000; i++) {
			data.clear();
			data.put("k_" + i, "v_" + i);
			jedis.hmset("key_" + i, data);
		}
		long end = System.currentTimeMillis();
		System.out.println("dbsize:[" + jedis.dbSize() + "] .. ");
		System.out.println("hmset without pipeline used [" + (end - start) + "] milliseconds ..");
		jedis.select(8);
		jedis.flushDB();
		// 使用pipeline hmset
		Pipeline p = jedis.pipelined();
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			data.clear();
			data.put("k_" + i, "v_" + i);
			p.hmset("key_" + i, data);
		}
		p.sync();
		end = System.currentTimeMillis();
		System.out.println("dbsize:[" + jedis.dbSize() + "] .. ");
		System.out.println("hmset with pipeline used [" + (end - start) + "] milliseconds ..");

		// hmget
		Set<String> keys = jedis.keys("*");
		// 直接使用Jedis hgetall
		start = System.currentTimeMillis();
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		for (String key : keys) {
			result.put(key, jedis.hgetAll(key));
		}
		end = System.currentTimeMillis();
		System.out.println("result size:[" + result.size() + "] ..");
		System.out.println("hgetAll without pipeline used [" + (end - start) + "] milliseconds ..");

		// 使用pipeline hgetall
		Map<String, Response<Map<String, String>>> responses = new HashMap<String, Response<Map<String, String>>>(
				keys.size());
		result.clear();
		start = System.currentTimeMillis();
		for (String key : keys) {
			responses.put(key, p.hgetAll(key));
		}
		p.sync();
		for (String k : responses.keySet()) {
			result.put(k, responses.get(k).get());
		}
		end = System.currentTimeMillis();
		System.out.println("result size:[" + result.size() + "] ..");
		System.out.println("hgetAll with pipeline used [" + (end - start) + "] milliseconds ..");

		jedis.disconnect();

	}

}
