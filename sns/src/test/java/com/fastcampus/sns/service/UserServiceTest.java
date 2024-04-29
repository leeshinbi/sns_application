package com.fastcampus.sns.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.UserEntityRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserEntityRepository userEntityRepository;

	@Test
	void 회원가입이_정상적으로_동작하는_경우() {
		String userName = "userName";
		String password = "password";

		//moking
		//회원가입이 안된 유저기 때문에 empty가 나와야 정상
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
		when(userEntityRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(userName, password)));

		Assertions.assertDoesNotThrow(() -> userService.join(userName, password)); // 에러 throw(x)
	}

	@Test
	void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
		String userName = "userName";
		String password = "password";

		//가짜 entity
		UserEntity fixture = UserEntityFixture.get(userName, password);

		//moking
		//회원가입이 된 유저가 이미 있는 상황이므로, 어떤 유저가 반환이 되어야 정상
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
		when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

		// Exception 반환
		Assertions.assertThrows(SnsApplicationException.class,
			() -> userService.join(userName, password));
	}

	///////// 로그인
	@Test
	void 로그인이_정상적으로_동작하는_경우() {
		String userName = "userName";
		String password = "password";

		//가짜 entity
		UserEntity fixture = UserEntityFixture.get(userName, password);

		//moking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

		Assertions.assertDoesNotThrow(() -> userService.login(userName, password)); // 에러 throw(x)
	}

	@Test
	void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
		String userName = "userName";
		String password = "password";

		//moking
		//해당 userName으로 가입한 유저가 없는 경우에는 empty값 반환이 정상
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

		// Exception 반환
		Assertions.assertThrows(SnsApplicationException.class,
			() -> userService.login(userName, password));
	}

	@Test
	void 로그인시_패스워드가_틀린_경우() { //가입한 유저는 존재하는 상황
		String userName = "userName";
		String password = "password";
		String wrongPassword = "wrongPassword"; // 틀린 패스워드

		//가짜 entity
		UserEntity fixture = UserEntityFixture.get(userName, password);

		//moking
		when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

		// Exception 반환
		Assertions.assertThrows(SnsApplicationException.class,
			() -> userService.login(userName, wrongPassword)); // 틀린 패스워드 넣어줌
	}
}
