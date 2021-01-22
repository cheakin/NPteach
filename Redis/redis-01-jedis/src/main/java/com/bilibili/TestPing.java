package com.bilibili;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TestPing {
    public static void main(String[] args) {
        // 1.new Jedis对象即可
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        
        //redis所有的指令就是jedis对象的方法
        System.out.println(jedis.ping());   //输出PONG


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello", "world");
        jsonObject.put("name", "zhangsan");
        //事务
        Transaction multi = jedis.multi();
        String result = jsonObject.toJSONString();
        try {
            multi.set("user1",  result);
            multi.set("user2", result);

            multi.exec();   //执行事务
        } catch (Exception e) {
            multi.discard();    //放弃事务
            e.printStackTrace();
        } finally {
            System.out.println(jedis.get("user1"));
            System.out.println(jedis.get("user2"));
            jedis.close();
        }
    }
}
