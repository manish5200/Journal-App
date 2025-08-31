package net.manifest.journalapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    public <T> T get(String key, Class<T> entityClass){
           try{
               Object o = redisTemplate.opsForValue().get(key);
               if(o == null){
                    return  null;
               }
               return entityClass.cast(o);
           } catch (Exception e) {
               log.error("Error in getting value from Redis for key {}",key,e);
               return  null;
           }
    }

    public void set(String key, Object o ,Long ttl){
        try{
           // String jsonValue = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,o,ttl, TimeUnit.SECONDS);
            log.info("Saved to Redis with key: {}",key);
        } catch (Exception e) {
            log.error("Error setting value to Redis for key {}", key, e);
        }
    }


}
