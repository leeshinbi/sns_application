package com.fastcampus.sns.contoller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJoinRequest { // 회원가입 시 리퀘스트 바디로 데이터를 받아올 때 사용

	private String name;
	private String password;

}
