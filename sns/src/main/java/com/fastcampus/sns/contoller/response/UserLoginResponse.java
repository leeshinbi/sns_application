package com.fastcampus.sns.contoller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginResponse {

	private String token; // 로그인이 정상적으로 이루어졌다면, 토큰 반환

}
