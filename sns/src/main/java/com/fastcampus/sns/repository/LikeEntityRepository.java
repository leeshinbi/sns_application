package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

	// 유저와 포스트 정보를 가지고 와서, 해당 유저가 이 포스트에 좋아요한 로우가 있는지를 확인
	Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

	// 좋아요 개수 가져오기 (변경 후)
	@Query(value = "SELECT COUNT(*) from LikeEntity entity WHERE entity.post = :post")
	Integer countByPost(@Param("post") PostEntity post);


	// 좋아요 개수 가져오기 (변경 전)
	List<LikeEntity> findAllByPost(PostEntity post);


}
