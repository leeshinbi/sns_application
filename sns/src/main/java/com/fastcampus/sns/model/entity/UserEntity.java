package com.fastcampus.sns.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table()
public class UserEntity {

	@Id
	private Integer id;
	@Column(name="user_name")
	private String userName;
	@Column(name="password")
	private String password;
}
