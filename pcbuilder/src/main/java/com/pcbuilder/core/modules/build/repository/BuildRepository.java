package com.pcbuilder.core.modules.build.repository;

import com.pcbuilder.core.modules.build.model.Build;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildRepository extends JpaRepository<Build, Long>, JpaSpecificationExecutor<Build> {
    Optional<Build> findByUserId(Long userId);
}
