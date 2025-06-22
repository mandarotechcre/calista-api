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

            String subject = "🌸 Yuk Cek Siklus Haid-mu di Calista!";
            String text = String.format("""
            Hai %s 👋,
        
            Kami mendeteksi bahwa siklus haid kamu saat ini masih berlangsung.
            Jangan lupa untuk terus memperbarui data siklus kamu secara rutin di Calista ya!
        
            ✨ Berikut pengingat lembut dari kami:
            • Tanggal mulai: %s
            • Durasi sampai hari ini: %d hari
            %s
        
            🌼 Jaga selalu kesehatanmu, dan biarkan Calista menjadi teman setiamu dalam memahami tubuhmu.
        
            Dengan kasih 💗,
            Tim Calista
            """,
                            user.getUsername(),
                            cycle.getStartDate(),
                            duration,
                            duration > 15 ? """
            
            ⚠️ Peringatan Penting:
            Durasi haid kamu telah melebihi 15 hari. Ini kemungkinan besar masuk kategori *istihadhah*.
            
            🕌 Dalam kondisi istihadhah, kamu **wajib melaksanakan ibadah wajib** seperti salat dan puasa.
            Silakan konsultasikan dengan tenaga medis atau ustadzah untuk panduan lebih lanjut.
            """ : ""
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
