package com.caizhilian.repository;

import com.caizhilian.entity.ContractInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractInfoRepository extends JpaRepository<ContractInfo, Long> {
    Optional<ContractInfo> findByContractId(Long contractId);
    void deleteByContractId(Long contractId);
}
