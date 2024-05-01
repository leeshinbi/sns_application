package com.fastcampus.sns.exception;

import com.fastcampus.sns.contoller.response.Response;
import com.fastcampus.sns.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

	/*SnsApplicationException 예외가 발생했을 때,
	그 예외를 로그에 기록하고, 적절한 HTTP 상태 코드와 오류 메시지를 클라이언트에게 반환*/
	@ExceptionHandler(SnsApplicationException.class)
	public ResponseEntity<?> applicationHandler(SnsApplicationException e) {
		log.error("Error occurs {}", e.toString());
		return ResponseEntity.status(e.getErrorCode().getStatus())
			.body(Response.error(e.getErrorCode().name()));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> applicationHandler(RuntimeException e) {
		log.error("Error occurs {}", e.toString());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));
	}


}
