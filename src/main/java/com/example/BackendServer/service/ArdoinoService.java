package com.example.BackendServer.service;

import com.example.BackendServer.dto.ardoino.response.ArdoinoUnlockDeviceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service @Slf4j
@RequiredArgsConstructor
public class ArdoinoService {

    private final static String ARDOINO_IP = "121.130.175.45/";

    private final WebClient webClient;
    public ArdoinoUnlockDeviceResponse unlockDivice(Long id) {

        String block = webClient.post()

                .uri(ARDOINO_IP + String.valueOf(id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ArdoinoUnlockDeviceResponse.builder()
                .deviceId(String.valueOf(id))
                .build();
    }
}
