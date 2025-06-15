package com.mndro.calista.service.impl;

import com.mndro.calista.entity.MenstrualCycle;
import com.mndro.calista.entity.User;
import com.mndro.calista.repository.MenstrualCycleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final MenstrualCycleRepository cycleRepository;
    private final EmailServiceImpl emailService;

    public int sendReminderToUsersWithActiveCycles() {
        List<MenstrualCycle> activeCycles = cycleRepository.findAllActiveCycles()
                .stream()
                .filter(MenstrualCycle::getIsActive)
                .toList();

        for (MenstrualCycle cycle : activeCycles) {
            User user = cycle.getUser();
            String email = user.getUsername(); // assuming username is email

            long duration = ChronoUnit.DAYS.between(cycle.getStartDate(), LocalDate.now());

            // Tandai istihadah jika durasi lebih dari 15 hari
            if (duration > 15 && !Boolean.TRUE.equals(cycle.getIsIstihaadhah())) {
                cycle.setIsIstihaadhah(true);
                cycle.setDuration((int) duration); // opsional update durasi
                cycleRepository.save(cycle);
                log.info("Marked istihadah for user {} on cycle {}", user.getUsername(), cycle.getId());
            }

            String subject = "🌸 Keep Tracking Your Cycle with Calista!";
            String text = String.format("""
                    Hi %s 👋,

                    We noticed your menstrual cycle is currently active.
                    Don't forget to update your cycle data regularly in Calista!

                    ✨ Here's a gentle reminder:
                    • Started on: %s
                    • Duration so far: %d days
                    %s

                    Stay healthy and let Calista be your gentle companion in understanding your body.

                    With love 💗,
                    The Calista Team
                    """,
                    user.getUsername(),
                    cycle.getStartDate(),
                    duration,
                    duration > 15 ? "\n⚠️ Your cycle has passed 15 days. This may be considered *istihaadhah*.\nPlease consult with your doctor or religious advisor for further guidance." : ""
            );

            try {
                emailService.sendEmail(email, subject, text);
                log.info("Sent reminder to: {}", email);
            } catch (Exception e) {
                log.error("Failed to send email to: {}", email, e);
            }
        }

        return activeCycles.size();
    }
}
