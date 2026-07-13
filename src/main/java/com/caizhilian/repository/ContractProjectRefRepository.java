package com.caizhilian.repository;

import com.caizhilian.entity.ContractProjectRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractProjectRefRepository extends JpaRepository<ContractProjectRef, Long> {
    List<ContractProjectRef> findByContractId(Long contractId);
    void deleteByContractId(Long contractId);
}
