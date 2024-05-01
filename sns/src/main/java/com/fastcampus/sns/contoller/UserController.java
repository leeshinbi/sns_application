package com.fastcampus.sns.contoller;

import com.fastcampus.sns.contoller.request.UserJoinRequest;
import com.fastcampus.sns.contoller.response.Response;
import com.fastcampus.sns.contoller.response.UserJoinResponse;
import com.fastcampus.sns.model.dto.User;
import com.fastcampus.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/join")
	public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
		User user = userService.join(request.getUserName(),request.getPassword());
		return Response.success(UserJoinResponse.fromUser(user));
	}

}
