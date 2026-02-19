package com.pcbuilder.core.modules.social.repository;

import com.pcbuilder.core.modules.social.model.Bookmark;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndBuildId(Long userId, Long buildId);

    boolean existsByUserIdAndBuildId(Long userId, Long buildId);

    @EntityGraph(attributePaths = {"build", "build.user"})
    List<Bookmark> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteAllByBuildId(Long buildId);

    @Query("SELECT b.build.id FROM Bookmark b WHERE b.user.id = :userId AND b.build.id IN :buildIds")
    List<Long> findBuildIdsBookmarkedByUser(@Param("userId") Long userId, @Param("buildIds") List<Long> buildIds);
}