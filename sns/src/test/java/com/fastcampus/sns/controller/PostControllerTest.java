package com.fastcampus.sns.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fastcampus.sns.contoller.request.PostCreateRequest;
import com.fastcampus.sns.contoller.request.PostModifyRequest;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.exception.enums.ErrorCode;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.model.dto.Post;
import com.fastcampus.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PostService postService;

	@Test
	@WithMockUser // 인증된 사용자로 포스트 작성
	void 포스트_작성_성공한_경우() throws Exception {

		String title = "title";
		String body = "body";

		mockMvc.perform(post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				// `UserJoinRequest` 객체를 JSON 바이트 배열로 직렬화
				.content(objectMapper.writeValueAsBytes(new PostCreateRequest(title,body)))
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser // 익명의 유저로 이 요청을 날렸을 경우
	void 포스트작성시_로그인하지_않은_경우_에러발생() throws Exception {

		String title = "title";
		String body = "body";

		mockMvc.perform(post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostCreateRequest(title,body)))
			).andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
	}

	///////////////// 포스트 수정

	@Test
	@WithMockUser
	void 포스트_수정_성공한_경우() throws Exception {

		String title = "title";
		String body = "body";

		when(postService.modify(eq(title), eq(body), any(), any())).
			thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1)));

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title,body)))
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser
	void 포스트수정시_로그인하지_않은_경우_에러발생() throws Exception {

		String title = "title";
		String body = "body";

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title,body)))
			).andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value())); // 정상 동작
	}

	@Test
	@WithMockUser
	void 포스트_수정시_본인이_작성한_게시글이_아닌경우_에러발생() throws Exception {

		String title = "title";
		String body = "body";

		//mocking
		doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title),eq(body),any(),eq(1));

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
			.andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
	}

	@Test
	@WithMockUser
	void 포스트_수정시_수정하려는_게시물이_없는_경우_에러발생() throws Exception {

		String title = "title";
		String body = "body";

		//mocking
		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(title),eq(body),any(),eq(1));

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title,body)))
			).andDo(print())
			.andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
	}

////////////////포스트 삭제

	@Test
	@WithMockUser
	void 포스트_삭제_성공한_경우() throws Exception {

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser
	void 포스트_삭제시_로그인하지_않은_경우() throws Exception {

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	void 포스트_삭제시_작성자와_삭제요청자가_다를_경우() throws Exception {

		//mocking
		doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService)
			.delete(any(), any());

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	void 포스트_삭제시_삭제하려는_포스트가_존재하지_않는_경우() throws Exception {

		//mocking
		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService)
			.delete(any(), any());

		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isNotFound());
	}
}
