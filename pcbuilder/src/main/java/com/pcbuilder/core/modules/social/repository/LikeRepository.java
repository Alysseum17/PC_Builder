package com.pcbuilder.core.modules.social.repository;

import com.pcbuilder.core.modules.social.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndBuildId(Long userId, Long buildId);

    boolean existsByUserIdAndBuildId(Long userId, Long buildId);

    long countByBuildId(Long buildId);

    void deleteAllByBuildId(Long buildId);

    @Query("SELECT l.build.id, COUNT(l) FROM Like l WHERE l.build.id IN :buildIds GROUP BY l.build.id")
    List<Object[]> countByBuildIds(@Param("buildIds") List<Long> buildIds);

    @Query("SELECT l.build.id FROM Like l WHERE l.user.id = :userId AND l.build.id IN :buildIds")
    List<Long> findBuildIdsLikedByUser(@Param("userId") Long userId, @Param("buildIds") List<Long> buildIds);
}