package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.CommentEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {

	Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

	@Transactional
	@Modifying
	@Query("UPDATE CommentEntity entity SET entity.removedAt = CURRENT_TIMESTAMP where entity.post = :post")
	void deleteAllByPost(@Param("post") PostEntity post);
}
