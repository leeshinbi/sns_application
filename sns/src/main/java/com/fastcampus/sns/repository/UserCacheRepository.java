package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.dto.User;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

	private final RedisTemplate<String, User> userRedisTemplate;

	private final static Duration USER_CACHE_TTL = Duration.ofDays(3); //Expired Time 설정(3일)


	public void setUser(User user) {
		String key = getKey(user.getUsername());
		log.info("Set User to Redis {}({})", key, user);
		userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
	}

	public Optional<User> getUser(String userName) {
		User data = userRedisTemplate.opsForValue().get(getKey(userName));
		log.info("Get User from Redis {}", data);
		return Optional.ofNullable(data);
	}


	private String getKey(String userName) {
		return "UID:" + userName;
	}
}
