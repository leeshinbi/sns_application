package com.fastcampus.sns.contoller;

import com.fastcampus.sns.contoller.request.PostCreateRequest;
import com.fastcampus.sns.contoller.request.PostModifyRequest;
import com.fastcampus.sns.contoller.response.PostResponse;
import com.fastcampus.sns.contoller.response.Response;
import com.fastcampus.sns.model.dto.Post;
import org.springframework.data.domain.Pageable;
import com.fastcampus.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping("")
	public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
		postService.create(request.getTitle(), request.getBody(), authentication.getName());
		return Response.success();
	}

	@PutMapping("/{postId}")
	public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
		Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(),postId);
		return Response.success(PostResponse.fromPost(post));
	}

	@DeleteMapping("/{postId}")
	public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
		postService.delete(authentication.getName(), postId);
		return Response.success();
	}

	@GetMapping // 모든 유저의 모든 게시물 조회
	public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
		return Response.success(postService.list(pageable).map(PostResponse::fromPost));
	}

	@GetMapping("/my") // 내 모든 게시물 조회
	public Response<Page<PostResponse>> myPosts(Pageable pageable, Authentication authentication) {
		return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
	}


}
