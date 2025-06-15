package com.mndro.calista.service.impl;


import com.mndro.calista.data.ActionType;
import com.mndro.calista.data.dto.ActionRequestDTO;
import com.mndro.calista.entity.MenstrualCycle;
import com.mndro.calista.entity.User;
import com.mndro.calista.repository.EstimatedCycleRepository;
import com.mndro.calista.repository.MenstrualCycleRepository;
import com.mndro.calista.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenstrualCycleApiService {
    private final UserRepository userRepository;
    private final MenstrualCycleRepository menstrualCycleRepository;
    private final EstimatedCycleRepository estimatedCycleRepository;

    public ResponseEntity<?> markIt( ActionRequestDTO actionRequestDTO, User user ) {
        LocalDate actionDate = LocalDate.parse(actionRequestDTO.getDate());
        String actionType = actionRequestDTO.getActionType();

        MenstrualCycle currentCycle = menstrualCycleRepository.findTopByUserAndIsActiveTrueOrderByStartDateDesc(user);
        MenstrualCycle lastEndedCycle = menstrualCycleRepository.findTopByUserAndIsActiveFalseOrderByEndDateDesc(user);

        String baseUrl = "http://localhost:5000";

        switch (actionType) {
            case ActionType.START:
                LocalDate defaultLastEnd = actionDate.minusDays(28);
                Integer defaultCycleLength = 28;
                Integer defaultDuration = 7;

                // Jika user benar-benar baru
                if (currentCycle == null && lastEndedCycle == null) {
                    LocalDate dummyStart = defaultLastEnd.minusDays(defaultCycleLength - 1);

                    MenstrualCycle dummy = MenstrualCycle.builder()
                            .user(user)
                            .startDate(dummyStart)
                            .endDate(defaultLastEnd)
                            .duration(defaultDuration)
                            .cycleLength(defaultCycleLength)
                            .isIstihaadhah(false)
                            .isActive(false)
                            .build();
                    menstrualCycleRepository.save(dummy);

                    // Kirim dummy ke Python
                    this.sendToAddCycleAPI(baseUrl, user.getUserId(), dummy);

                    MenstrualCycle firstCycle = MenstrualCycle.builder()
                            .user(user)
                            .startDate(actionDate)
                            .lastMenstrual(defaultLastEnd)
                            .cycleLength(defaultCycleLength)
                            .isActive(true)
                            .build();
                    menstrualCycleRepository.save(firstCycle);

                    return ResponseEntity.ok("Siklus pertama dan dummy dibuat");
                }

                if (currentCycle != null) {
                    currentCycle.setIsActive(false);
                    currentCycle.setEndDate(actionDate.minusDays(1));
                    currentCycle.setDuration((int) ChronoUnit.DAYS.between(currentCycle.getStartDate(), actionDate.minusDays(1)) + 1);
                    currentCycle.setIsIstihaadhah(currentCycle.getDuration() > 15);
                    menstrualCycleRepository.save(currentCycle);
                }

                LocalDate lastMenstrualDate = lastEndedCycle != null ? lastEndedCycle.getEndDate() : defaultLastEnd;
                int cycleLength = lastEndedCycle != null
                        ? (int) ChronoUnit.DAYS.between(lastMenstrualDate, actionDate)
                        : defaultCycleLength;

                MenstrualCycle newCycle = MenstrualCycle.builder()
                        .user(user)
                        .startDate(actionDate)
                        .lastMenstrual(lastMenstrualDate)
                        .cycleLength(cycleLength)
                        .isActive(true)
                        .build();
                menstrualCycleRepository.save(newCycle);

                return ResponseEntity.ok("Siklus baru dimulai");

            case ActionType.PEAK:
                if (currentCycle == null) {
                    return ResponseEntity.badRequest().body("Belum ada siklus aktif. Mulai siklus terlebih dahulu.");
                }
                currentCycle.setPeakDate(actionDate);
                menstrualCycleRepository.save(currentCycle);
                return ResponseEntity.ok("Tanggal puncak ditandai");

            case ActionType.END:
                if (currentCycle == null) {
                    return ResponseEntity.badRequest().body("Belum ada siklus aktif untuk diakhiri.");
                }
                if (actionDate.isBefore(currentCycle.getStartDate())) {
                    return ResponseEntity.badRequest().body("Tanggal akhir tidak valid.");
                }

                currentCycle.setEndDate(actionDate);
                currentCycle.setIsActive(false);
                currentCycle.setDuration((int) ChronoUnit.DAYS.between(currentCycle.getStartDate(), actionDate) + 1);
                currentCycle.setIsIstihaadhah(currentCycle.getDuration() > 15);
                menstrualCycleRepository.save(currentCycle);

                // Kirim data selesai ke add-cycle
                this.sendToAddCycleAPI(baseUrl, user.getUserId(), currentCycle);

                // Retrain dan prediksi
                this.retrainAndPredict(baseUrl, user.getUserId());

                return ResponseEntity.ok("Siklus diakhiri dan model diperbarui");

            default:
                return ResponseEntity.badRequest().body("Aksi tidak dikenali.");
        }
    }


    public void sendToAddCycleAPI(String baseUrl, UUID userId, MenstrualCycle cycle) {
        String url = baseUrl + "/add-cycle/" + userId;
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> payload = new HashMap<>();
        payload.put("start_date", cycle.getStartDate().toString());
        payload.put("durasi", cycle.getDuration());
        payload.put("hari_max_volume", cycle.getPeakDate() != null
                ? ChronoUnit.DAYS.between(cycle.getStartDate(), cycle.getPeakDate()) + 1
                : (cycle.getDuration() / 2));
        payload.put("panjang_siklus", cycle.getCycleLength());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            System.err.println("Gagal menambahkan ke add-cycle: " + e.getMessage());
        }
    }

    public void retrainAndPredict(String baseUrl, UUID userId) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            String trainUrl = baseUrl + "/train/" + userId;
            restTemplate.getForEntity(trainUrl, String.class);

            String predictUrl = baseUrl + "/predict/" + userId;
            restTemplate.postForEntity(predictUrl, null, String.class);
        } catch (Exception e) {
            System.err.println("Gagal retrain atau predict: " + e.getMessage());
        }
    }


}
