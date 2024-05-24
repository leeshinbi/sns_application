package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.exception.enums.ErrorCode;
import com.fastcampus.sns.model.dto.Post;
import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.LikeEntityRepository;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostEntityRepository postEntityRepository;
	private final UserEntityRepository userEntityRepository;
	private final LikeEntityRepository likeEntityRepository;

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

	// 모든 유저의 모든 피드 목록
	public Page<Post> list(Pageable pageable) {
		return postEntityRepository.findAll(pageable).map(Post::fromEntity);
	}

	// 내 모든 피드 목록
	public Page<Post> my(String userName, Pageable pageable) {

		//유저 찾기
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s not founded", userName)));

		return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
	}

	// 피드 좋아요
	@Transactional
	public void like(Integer postId, String userName) {
		//유저 찾기
		UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s not founded", userName)));

		//게시글이 존재하는지 확인
		PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.POST_NOT_FOUND,
				String.format("%s not founded", postId)));

		// 이미 좋아요를 눌렀는지 체크 -> ifPresent -> throw exception
		likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
			throw new SnsApplicationException(ErrorCode.ALREADY_LIKED_POST,
				String.format("userName %s already like the post %s", userName, postId));
		});


		// like save
		likeEntityRepository.save(LikeEntity.of(postEntity, userEntity));

	}

	// 피드 좋아요 개수
	public Integer getLikeCount(Integer postId) {

		//게시글이 존재하는지 확인
		PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
			new SnsApplicationException(ErrorCode.POST_NOT_FOUND,
				String.format("%s not founded", postId)));

		// 좋아요 개수 가져오기 (변경 후)
		return likeEntityRepository.countByPost(postEntity);

		// 좋아요 개수 가져오기 (변경 전)
		/*List<LikeEntity> likes = likeEntityRepository.findAllByPost(postEntity);
		return likes.size();*/


	}


}
