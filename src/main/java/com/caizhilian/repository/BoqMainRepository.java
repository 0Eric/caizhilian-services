package com.caizhilian.repository;

import com.caizhilian.entity.BoqMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoqMainRepository extends JpaRepository<BoqMain, Long> {
    Optional<BoqMain> findByBoqId(Long boqId);
    void deleteByBoqId(Long boqId);
}
