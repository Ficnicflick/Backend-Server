package com.example.BackendServer.service;

import com.example.BackendServer.dto.ardoino.request.ArdoinoRequest;
import com.example.BackendServer.dto.ardoino.response.ArdoinoDeviceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service @Slf4j
@RequiredArgsConstructor
public class ArdoinoService {

    private final static String ARDOINO_IP = "172.20.10.3";
    private final static String LOCK = "/lock";
    private final static String UNLOCK = "/unlock";

    private final WebClient webClient;
    public ArdoinoDeviceResponse unlockDivice(ArdoinoRequest request) {

        ArdoinoDeviceResponse block = webClient.post()
                .uri(ARDOINO_IP + UNLOCK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ArdoinoDeviceResponse.class)
                .block();
        log.info("block.getMessage() = {}", block.getMessage());

        // block은 돗자리 id(기계 id[특정 기기])를 의미
        return ArdoinoDeviceResponse.builder()
                .message(block.getMessage())
                .build();
    }

    public ArdoinoDeviceResponse lockDivice(ArdoinoRequest request) {

        ArdoinoDeviceResponse block = webClient.post()
                .uri(ARDOINO_IP + LOCK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ArdoinoDeviceResponse.class)
                .block();
        log.info("block.getMessage() = {}", block.getMessage());

        // block은 돗자리 id(기계 id[특정 기기])를 의미
        return ArdoinoDeviceResponse.builder()
                .message(block.getMessage())
                .build();
    }
}
