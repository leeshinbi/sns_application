package com.fastcampus.sns.contoller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

	private String resultCode; // 결과에 대한 코드값
	private T result; // 만약 결과가 있다면, result 값을 내려준다.

	// 에러일 경우는, 에러 코드만 내려줌
	public static Response<Void> error(String errorCode) {
		return new Response<>(errorCode, null);
	}

	// 성공일 경우엔, 다양한 result 형태가 들어가기 때문에 <T> 반환
	public static <T> Response<T> success() {
		return new Response<T>("SUCCESS", null);
	}
	public static <T> Response<T> success(T result) {
		return new Response<T>("SUCCESS", result);
	}

	public String toStream() {
		if (result == null) {
			return "{" +
				"\"resultCode\":" + "\"" + resultCode + "\"," +
				"\"result\":" + null +
				"}";
		}
		return "{" +
			"\"resultCode\":" + "\"" + resultCode + "\"," +
			"\"result\":" + "\"" + result + "\"," +
			"}";
	}
}
