package com.fastcampus.sns.contoller;

import com.fastcampus.sns.contoller.request.UserJoinRequest;
import com.fastcampus.sns.contoller.request.UserLoginRequest;
import com.fastcampus.sns.contoller.response.Response;
import com.fastcampus.sns.contoller.response.UserJoinResponse;
import com.fastcampus.sns.contoller.response.UserLoginResponse;
import com.fastcampus.sns.model.dto.User;
import com.fastcampus.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/join")
	public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
		User user = userService.join(request.getName(), request.getPassword());
		return Response.success(UserJoinResponse.fromUser(user));
	}


	@PostMapping("/login")
	public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
		String token = userService.login(request.getName(), request.getPassword());
		log.debug("Generated token: {}", token); // 이 줄 추가
		return Response.success(new UserLoginResponse(token));
	}
}
