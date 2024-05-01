package com.fastcampus.sns.contoller.response;

import com.fastcampus.sns.model.dto.User;
import com.fastcampus.sns.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public
class UserJoinResponse {
	private Integer id;
	private String name;

	public static UserJoinResponse fromUser(User user) {
		return new UserJoinResponse(
			user.getId(),
			user.getUsername()
		);
	}

}