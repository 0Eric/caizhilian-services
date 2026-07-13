package com.caizhilian.repository;

import com.caizhilian.entity.BoqDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoqDetailRepository extends JpaRepository<BoqDetail, Long> {
    List<BoqDetail> findByBoqId(Long boqId);
    void deleteByBoqId(Long boqId);
}
