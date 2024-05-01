package com.fastcampus.sns.model.dto;

import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User { //dto


	private Integer id;
	private String username;
	private String password;
	private UserRole role;
	private Timestamp registeredAt;
	private Timestamp updatedAt;
	private Timestamp removedAt;


	// Entity -> Dto 변환
	public static User fromEntity(UserEntity entity) {
		return new User(
			entity.getId(),
			entity.getUserName(),
			entity.getPassword(),
			entity.getRole(),
			entity.getRegisteredAt(),
			entity.getUpdatedAt(),
			entity.getRemovedAt()
		);
	}

}
