package com.fastcampus.sns.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.exception.enums.ErrorCode;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PostServiceTest {

	@Autowired
	private PostService postService;

	@MockBean
	private PostEntityRepository postEntityRepository;
	@MockBean
	private UserEntityRepository userEntityRepository;

	@Test
	void 포스트_작성_성공한_경우() {

		String title = "title";
		String body = "body";
		String userName = "userName";

		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
		//mocking
		when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

		Assertions.assertDoesNotThrow(() ->postService.create(title, body, userName));

	}

	@Test
	void 포스트작성시_요청한유저가_존재하지_않는_경우() {

		String title = "title";
		String body = "body";
		String userName = "userName";

		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
		//mocking
		when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.create(title, body, userName));
		Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	void 포스트_수정_성공한_경우() {

		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity userEntity = postEntity.getUser();


		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
		//mocking
		when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

		Assertions.assertDoesNotThrow(() ->postService.modify(title, body, userName,postId));

	}

	@Test
	void 포스트_수정시_포스트가_존재하지_않으면_에러발생() {

		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity userEntity = postEntity.getUser();


		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.modify(title, body, userName,postId));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

	}

	@Test
	void 포스트_수정시_포스트_작성자와_유저가_일치하지_않으면_에러발생() {

		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity writer = UserEntityFixture.get("userName1", "password");



		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.modify(title, body, userName,postId));
		Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

	}

	//아직 not 구현
	@Test
	void 포스트_수정시_유저가_존재하지_않으면_에러발생() {

		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity userEntity = postEntity.getUser();


		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.modify(title, body, userName,postId));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

	}

////////////////////// 포스트 삭제

	@Test
	void 포스트_삭제_성공한_경우() {

		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity userEntity = postEntity.getUser();


		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		Assertions.assertDoesNotThrow(() ->postService.delete(userName,1));

	}

	@Test
	void 포스트_삭제시_포스트가_존재하지_않는_경우() {

		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity userEntity = postEntity.getUser();


		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() ->postService.delete(userName,1));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

	}

	@Test
	void 포스트_삭제시_권한이_없는_경우() {

		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId);
		UserEntity writer = UserEntityFixture.get("userName1", "password");


		//mocking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
		//mocking
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() ->postService.delete(userName,1));
		Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

	}
}
