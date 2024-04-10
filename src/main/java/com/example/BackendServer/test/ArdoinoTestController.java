package com.example.BackendServer.test;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
public class ArdoinoTestController {

    private final WebClient webClient;
    @PostMapping("/")
    public String test(){
        String block = webClient.post()
                .uri("121.130.175.45/")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return "성공!! - " + block + " [끝]";
    }
}
