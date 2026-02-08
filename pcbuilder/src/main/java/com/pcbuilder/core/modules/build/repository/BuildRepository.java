package com.pcbuilder.core.modules.build.repository;

import com.pcbuilder.core.modules.build.model.Build;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildRepository extends JpaRepository<Build, Long>, JpaSpecificationExecutor<Build> {
    Optional<Build> findByUserId(Long userId);

    @Query("SELECT b FROM Build b WHERE b.userId = :targetUserId AND (b.isPrivate = false OR b.userId = :requesterId)")
    List<Build> findAllVisibleBuilds(@Param("targetUserId") Long targetUserId, @Param("requesterId") Long requesterId);
}
