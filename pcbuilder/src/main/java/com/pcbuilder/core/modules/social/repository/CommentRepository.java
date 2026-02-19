package com.pcbuilder.core.modules.social.repository;

import com.pcbuilder.core.modules.social.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByBuildIdOrderByCreatedAtDesc(Long buildId, Pageable pageable);

    long countByBuildId(Long buildId);

    void deleteAllByBuildId(Long buildId);

    @Query("SELECT c.build.id, COUNT(c) FROM Comment c WHERE c.build.id IN :buildIds GROUP BY c.build.id")
    List<Object[]> countByBuildIds(@Param("buildIds") List<Long> buildIds);
}