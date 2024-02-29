package com.example.mutualtlsexample.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final NativeHttpClient client;

    @GetMapping(value = "/api/test", produces = "text/plain")
    public String callTls() {
        return client.callServerTls();
    }
}
