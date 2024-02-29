package com.example.mutualtlsexample.server;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    @GetMapping(value = "/api/tls-only", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> testTls(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body("Success. Your name is " + principal.getName());
    }
}
