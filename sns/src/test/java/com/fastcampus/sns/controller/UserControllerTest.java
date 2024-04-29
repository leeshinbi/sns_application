package com.fastcampus.sns.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fastcampus.sns.contoller.request.UserJoinRequest;
import com.fastcampus.sns.contoller.request.UserLoginRequest;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@Test
	public void 회원가입() throws Exception{
		String userName = "userName";
		String password ="password";

		when(userService.join(userName,password)).thenReturn(mock(User.class));

		mockMvc.perform(post("/api/v1/users/join")
				.contentType(MediaType.APPLICATION_JSON)
			// `UserJoinRequest` 객체를 JSON 바이트 배열로 직렬화
				.content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName,password)))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는경우_에러반환() throws Exception{
		String userName = "userName";
		String password ="password";

		when(userService.join(userName,password)).thenThrow(new SnsApplicationException());

		//TODO :
		mockMvc.perform(post("/api/v1/users/join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName,password)))
			).andDo(print())
			.andExpect(status().isConflict());
	}

	///////// 로그인
	@Test
	public void 로그인() throws Exception{
		String userName = "userName";
		String password ="password";

		when(userService.login(userName,password)).thenReturn("test_token");

		mockMvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				// `UserJoinRequest` 객체를 JSON 바이트 배열로 직렬화
				.content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName,password)))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void 로그인시_회원가입이_안된_userName을_입력할경우_에러반환() throws Exception{
		String userName = "userName";
		String password ="password";

		when(userService.login(userName,password)).thenThrow(new SnsApplicationException());

		mockMvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				// `UserJoinRequest` 객체를 JSON 바이트 배열로 직렬화
				.content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName,password)))
			).andDo(print())
			.andExpect(status().isNotFound()); // 유저를 찾지 못했기 때문에 isNotFound 반환
	}

	@Test
	public void 로그인시_틀린_password를_입력할경우_에러반환() throws Exception{
		String userName = "userName";
		String password ="password";

		when(userService.login(userName,password)).thenThrow(new SnsApplicationException());

		mockMvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				// `UserJoinRequest` 객체를 JSON 바이트 배열로 직렬화
				.content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName,password)))
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}
}
