package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.exception.enums.ErrorCode;
import com.fastcampus.sns.model.dto.User;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserEntityRepository userEntityRepository;
	private final BCryptPasswordEncoder encoder;

	@Transactional
	public User join(String userName, String password) {
		// 회원 가입하려는 userName으로 회원가입된 user가 있는지
		userEntityRepository.findByUserName(userName).ifPresent(it -> {
			throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
		});


		// 없다면, 회원 가입 진행 = 유저 등록
		UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName,encoder.encode(password)));
		throw new RuntimeException();
		// return User.fromEntity(userEntity);

	}

	public String login(String userName, String password) { // 토큰을 반환해야하기 때문에 반환값은 String
		// 회원가입 여부 체크
		UserEntity userEntity = userEntityRepository.findByUserName(userName)
			.orElseThrow(() -> new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

		// 회원가입이 완료된 유저라면, 비밀번호 체크
		if (!userEntity.getPassword().equals(password)) { // 등록된 비밀번호와 입력받은 비밀번호가 다를 경우
			throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, "");// 에러 반환
		}

		// 토큰 생성
		return "";
	}

}
