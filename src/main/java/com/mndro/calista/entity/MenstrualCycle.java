package com.mndro.calista.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenstrualCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate lastMenstrual;
    private Integer cycleLength;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate peakDate;
    private Integer duration;
    @Builder.Default
    private Boolean isIstihaadhah = false;
    private Boolean isActive = true;

    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }
 }
