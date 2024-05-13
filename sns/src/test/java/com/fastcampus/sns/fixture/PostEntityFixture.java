package com.fastcampus.sns.fixture;

import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;

public class PostEntityFixture {

	//가짜 테스트용 PostEntity
	public static PostEntity get(String userName, Integer postId) {
		UserEntity user = new UserEntity();
		user.setId(1);
		user.setUserName(userName);

		PostEntity result = new PostEntity();
		result.setUser(user);
		result.setId(postId);

		return result;
	}
}
