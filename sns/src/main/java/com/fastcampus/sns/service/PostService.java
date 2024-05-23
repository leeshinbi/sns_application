package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.exception.enums.ErrorCode;
import com.fastcampus.sns.model.dto.Post;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostEntityRepository postEntityRepository;
	private final UserEntityRepository userEntityRepository;

	@Transactional
	public void create(String title, String body, String userName) {

		//유저 찾기
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s not founded", userName)));
		//포스트 저장
		postEntityRepository.save(PostEntity.of(title, body, userEntity));

	}

	@Transactional
	public Post modify(String title, String body, String userName, Integer postId) {

		//유저 찾기
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s not founded", userName)));

		//게시글이 존재하는지 확인
		PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.POST_NOT_FOUND,
				String.format("%s not founded", postId)));

		//수정하려는 사람이 포스트를 작성한 사람인지 확인
		if (postEntity.getUser() != userEntity) {
			throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,
				String.format("%s has no permission with %s", userName, postId));
		}

		postEntity.setTitle(title);
		postEntity.setBody(body);
		return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));

	}

	@Transactional
	public void delete (String userName, Integer postId) {

		//유저 찾기
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s not founded", userName)));

		//게시글이 존재하는지 확인
		PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.POST_NOT_FOUND,
				String.format("%s not founded", postId)));

		//삭제하려는 사람이 포스트를 작성한 사람인지 확인
		if (postEntity.getUser() != userEntity) {
			throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,
				String.format("%s has no permission with %s", userName, postId));
		}

		postEntityRepository.delete(postEntity);
	}

}
