package com.fastcampus.sns.exception;

import com.fastcampus.sns.exception.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class SnsApplicationException extends RuntimeException{

	private ErrorCode errorCode;
	private String message;

	public SnsApplicationException(ErrorCode errorCode) { // 에러코드만 있고, 메세지는 필요 없는 경우 사용
		this.errorCode = errorCode;
		this.message = null;
	}

	@Override
	public String getMessage() {
		if (message == null) {
			return errorCode.getMessage();
		}

		return String.format("%s. %s", errorCode.getMessage(), message);
	}
}
