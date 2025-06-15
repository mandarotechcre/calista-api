package com.mndro.calista.repository;

import com.mndro.calista.entity.EstimatedCycle;
import com.mndro.calista.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstimatedCycleRepository extends JpaRepository<EstimatedCycle, Long> {
    Optional<EstimatedCycle> findByUser(User user);
    List<EstimatedCycle> findAllByUser(User user);
    List<EstimatedCycle> findAllByIsActiveTrue();
    Optional<EstimatedCycle> findTopByUserOrderByLastPeriodStartDateDesc(User user);
    void deleteAllByUser(User user);
    boolean existsByUserAndIsActiveTrue(User user);
}

