package com.mndro.calista.repository;

import com.mndro.calista.entity.MenstrualCycle;
import com.mndro.calista.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Long> {
    MenstrualCycle findTopByUserAndEndDateIsNullOrderByStartDateDesc(User user);
    MenstrualCycle findTopByUserAndEndDateIsNotNullOrderByEndDateDesc(User user);
    List<MenstrualCycle> findAllByUserOrderByStartDateDesc(User user);
    MenstrualCycle findTopByUserAndIsActiveTrueOrderByStartDateDesc(User user);
    MenstrualCycle findTopByUserAndIsActiveFalseOrderByEndDateDesc(User user);
    @Query("SELECT m FROM MenstrualCycle m WHERE m.isActive = true")
    List<MenstrualCycle> findAllActiveCycles();

}

