package com.fastcampus.sns.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Setter
@Getter
@Entity
@Table(name = "\"comment\"", indexes = {
	@Index(name = "post_id_idx", columnList = "post_id")
})
@SQLDelete(sql = "UPDATE \"comment\" SET removed_at = NOW() WHERE id=?")
@Where(clause = "removed_at is NULL")
@NoArgsConstructor
public class CommentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id = null;

	@Column(name = "comment")
	private String comment;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private PostEntity post;

	@Column(name = "registered_at")
	private Timestamp registeredAt;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "removed_at")
	private Timestamp removedAt;


	@PrePersist
	void registeredAt() {
		this.registeredAt = Timestamp.from(Instant.now());
	}

	@PreUpdate
	void updatedAt() {
		this.updatedAt = Timestamp.from(Instant.now());
	}

	public static CommentEntity of(String comment, PostEntity post, UserEntity user) {
		CommentEntity entity = new CommentEntity();
		entity.setComment(comment);
		entity.setPost(post);
		entity.setUser(user);
		return entity;
	}
}
