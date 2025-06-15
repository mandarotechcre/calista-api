package com.mndro.calista.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class EstimatedCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private Boolean isActive;

    private Integer estimatedCycleLength; // misal 28
    private Integer estimatedPeriodLength; // misal 6
    private LocalDate lastPeriodStartDate;
}
