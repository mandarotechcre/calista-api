package com.mndro.calista.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CyclePredictionService {

    private final RestTemplate restTemplate;

    public Map<String, Object> getPredictionFromPython(String userId) {
        String url = "http://localhost:5000/predict/" + userId;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            // Bisa log error di sini jika mau
        }

        // Jika gagal atau error status, kembalikan null
        return null;
    }
}

