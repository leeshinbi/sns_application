package com.fastcampus.sns.contoller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginRequest { // 로그인 시, 리퀘스트 바디로 데이터를 받아올 때 사용

	private String userName;
	private String password;

}
