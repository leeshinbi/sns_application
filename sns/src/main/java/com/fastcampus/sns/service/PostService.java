package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.exception.enums.ErrorCode;
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
		postEntityRepository.save(new PostEntity());

		//return

	}

}
