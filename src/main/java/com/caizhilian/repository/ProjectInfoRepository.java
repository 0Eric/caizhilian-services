package com.caizhilian.repository;

import com.caizhilian.entity.ProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, Long> {
    Optional<ProjectInfo> findByProjectId(Long projectId);
    void deleteByProjectId(Long projectId);
}
