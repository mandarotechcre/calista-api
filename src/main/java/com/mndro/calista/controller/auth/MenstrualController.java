package com.mndro.calista.controller.auth;

import com.mndro.calista.data.ActionType;
import com.mndro.calista.data.dto.ActionRequestDTO;
import com.mndro.calista.service.impl.CyclePredictionService;
import com.mndro.calista.service.impl.MenstrualCycleApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mndro.calista.entity.MenstrualCycle;
import com.mndro.calista.entity.EstimatedCycle;
import com.mndro.calista.entity.User;
import com.mndro.calista.repository.MenstrualCycleRepository;
import com.mndro.calista.repository.EstimatedCycleRepository;
import com.mndro.calista.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/menstrual")
@RequiredArgsConstructor
@Slf4j
public class MenstrualController {

    private final UserRepository userRepository;
    private final MenstrualCycleRepository menstrualCycleRepository;
    private final EstimatedCycleRepository estimatedCycleRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final MenstrualCycleApiService menstrualCycleApiService;

    private final CyclePredictionService cyclePredictionService;

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @GetMapping("" +
            "/active")
    public ResponseEntity<?> getCurrentActiveMenstrual() {
        User user = getCurrentUserEntity();
        MenstrualCycle activeCycle = menstrualCycleRepository.findTopByUserAndIsActiveTrueOrderByStartDateDesc(user);
        return ResponseEntity.ok(activeCycle);
    }

    @GetMapping("/get-current-prediction")
    public ResponseEntity<?> getCurrentPrediction() {
        User user = getCurrentUserEntity(); // ambil user login
        String userId = user.getUserId().toString();

        Map<String, Object> prediction = cyclePredictionService.getPredictionFromPython(userId);

        // Gunakan Collections.singletonMap agar aman meski prediction == null
        return ResponseEntity.ok(Collections.singletonMap("data", prediction));
    }


    @PostMapping("/mark-it")
    public ResponseEntity<?> markIt(@RequestBody ActionRequestDTO actionRequestDTO) {
        User user = getCurrentUserEntity();
        return menstrualCycleApiService.markIt(actionRequestDTO,user);
    }


    @GetMapping("/history")
    public ResponseEntity<?> getAllHistoryAndActive() {
        User user = getCurrentUserEntity();
        List<MenstrualCycle> allCycles = menstrualCycleRepository.findAllByUserOrderByStartDateDesc(user);

        // Optional: Tambahkan logika eksplisit jika ingin
        if (allCycles.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(allCycles);
    }


    @PostMapping("/start")
    public ResponseEntity<?> startNewCycle(@RequestBody Map<String, String> body) {
        // ketika start maka lakukan prediksi kapan behrakhsirnya yaa  dari data training nya

        User user = getCurrentUserEntity();

        MenstrualCycle existingActive = menstrualCycleRepository.findTopByUserAndIsActiveTrueOrderByStartDateDesc(user);
        if (existingActive != null) {
            existingActive.setIsActive(false);
            existingActive.setEndDate(LocalDate.now().minusDays(1));
            menstrualCycleRepository.save(existingActive);
        }

        LocalDate startDate = LocalDate.parse(body.get("startDate"));

        MenstrualCycle newCycle = MenstrualCycle.builder()
                .user(user)
                .startDate(startDate)
                .isActive(true)
                .build();

        menstrualCycleRepository.save(newCycle);
        return ResponseEntity.ok(Map.of("message", "Siklus baru dimulai", "cycleId", newCycle.getId()));
    }


    @PostMapping("/end")
    public ResponseEntity<?> endCurrentCycle(@RequestBody Map<String, String> body) {
        User user = getCurrentUserEntity();

        MenstrualCycle activeCycle = menstrualCycleRepository.findTopByUserAndIsActiveTrueOrderByStartDateDesc(user);
        if (activeCycle == null) return ResponseEntity.badRequest().body("Tidak ada siklus aktif");

        LocalDate endDate = LocalDate.parse(body.get("endDate"));
        LocalDate peakDate = LocalDate.parse(body.get("peakDate"));

        int duration = (int) ChronoUnit.DAYS.between(activeCycle.getStartDate(), endDate) + 1;

        activeCycle.setEndDate(endDate);
        activeCycle.setPeakDate(peakDate);
        activeCycle.setDuration(duration);
        activeCycle.setIsIstihaadhah(duration > 15);
        activeCycle.setIsActive(false);

        menstrualCycleRepository.save(activeCycle);

        // Call Python API to retrain model
        String url = pythonApiUrl + "/train/"+user.getUserId();
        Map<String, Object> predictionResult = restTemplate.postForObject(url, null, Map.class);

        // Save prediction to EstimatedCycle
        EstimatedCycle cycle = estimatedCycleRepository.findByUser(user).orElse(new EstimatedCycle());
        cycle.setUser(user);
        cycle.setIsActive(true);
        cycle.setEstimatedCycleLength((Integer) predictionResult.get("predicted_cycle_length"));
        cycle.setEstimatedPeriodLength((Integer) predictionResult.get("predicted_period_length"));
        cycle.setLastPeriodStartDate(activeCycle.getStartDate());

        estimatedCycleRepository.save(cycle);

        return ResponseEntity.ok(Map.of("message", "Siklus ditutup dan prediksi disimpan", "prediction", predictionResult));
    }

    private User getCurrentUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
