package com.pcbuilder.core.modules.build.repository;

import com.pcbuilder.core.modules.build.model.Build;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildRepository extends JpaRepository<Build, Long>, JpaSpecificationExecutor<Build> {

    @EntityGraph(attributePaths = {"user"})
    @Override
    Page<Build> findAll(Specification<Build> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Build> findById(Long id);

    Optional<Build> findByUserId(Long userId);

    @Query("SELECT b FROM Build b JOIN FETCH b.user WHERE b.user.id = :targetUserId AND (b.isPrivate = false OR b.user.id = :requesterId)")
    List<Build> findAllVisibleBuilds(@Param("targetUserId") Long targetUserId, @Param("requesterId") Long requesterId);
}
