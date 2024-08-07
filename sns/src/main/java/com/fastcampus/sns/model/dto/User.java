package com.fastcampus.sns.model.dto;

import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {

	private Integer id;
	private String username;
	private String password;
	private UserRole role;
	private Timestamp registeredAt;
	private Timestamp updatedAt;
	private Timestamp removedAt;


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

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.toString()));
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return removedAt == null;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return removedAt == null;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return removedAt == null;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return removedAt == null;
	}
}