package com.fastcampus.sns.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fastcampus.sns.contoller.request.PostCommentRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
	@WithAnonymousUser
	void 포스트삭제시_로그인한상태가_아니라면_에러발생() throws Exception {
		mockMvc.perform(delete("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
	}

	@Test
	@WithMockUser
	void 포스트삭제시_본인이_작성한_글이_아니라면_에러발생() throws Exception {
		doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), eq(1));
		mockMvc.perform(delete("/api/v1/posts/1")
				.header(HttpHeaders.AUTHORIZATION, "Bearer token")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
	}

	@Test
	@WithMockUser
	void 포스트삭제시_수정하려는글이_없다면_에러발생() throws Exception {
		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), eq(1));

		mockMvc.perform(delete("/api/v1/posts/1")
				.header(HttpHeaders.AUTHORIZATION, "Bearer token")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
	}

	/////////////////// 피드 조회

	@Test
	@WithMockUser
	void 피드목록_조회_성공한_경우() throws Exception {

		// mocking
		when(postService.list(any())).thenReturn(Page.empty());

		mockMvc.perform(get("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser
	void 피드목록_조회시_로그인하지_않은_경우() throws Exception {

		// mocking
		when(postService.list(any())).thenReturn(Page.empty());

		mockMvc.perform(get("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
	}

	@Test
	@WithMockUser
	void 내피드목록_조회_성공한_경우() throws Exception {

		// mocking
		when(postService.my(any(), any())).thenReturn(Page.empty());

		mockMvc.perform(get("/api/v1/posts/my")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser
	void 내피드목록_조회시_로그인하지_않은_경우() throws Exception {

		// mocking
		when(postService.my(any(), any())).thenReturn(Page.empty());

		mockMvc.perform(get("/api/v1/posts/my")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
	}

///////////////////////////// 피드 좋아요 기능

	@Test
	@WithMockUser
	void 좋아요_기능_성공한_경우() throws Exception {

		mockMvc.perform(post("/api/v1/posts/1/likes")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser
	void 좋아요버튼_클릭시_로그인하지_않은_경우() throws Exception {

		mockMvc.perform(post("/api/v1/posts/1/likes")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
	}

	@Test
	@WithMockUser
	void 좋아요버튼_클릭시_게시물이_없는_경우() throws Exception {

		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService)
			.like(any(), any());

		mockMvc.perform(post("/api/v1/posts/1/likes")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
	}

	///////////////////// 댓글 기능

	@Test
	@WithMockUser
	void 댓글_기능_성공한_경우() throws Exception {

		mockMvc.perform(post("/api/v1/posts/1/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment")))
			).andDo(print())
			.andExpect(status().isOk()); // 정상 동작
	}

	@Test
	@WithAnonymousUser
	void 댓글_작성시_로그인하지_않은_경우() throws Exception {

		mockMvc.perform(post("/api/v1/posts/1/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment")))
			).andDo(print())
			.andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
	}

	@Test
	@WithMockUser
	void 댓글_작성시_게시물이_없는_경우() throws Exception {

		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService)
			.comment(any(), any(),any());

		mockMvc.perform(post("/api/v1/posts/1/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment")))
			).andDo(print())
			.andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
	}


}
