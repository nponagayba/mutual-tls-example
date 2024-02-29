package com.example.mutualtlsexample.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class NativeHttpClient {

    private final KeyManagerFactory keyManagerFactory;
    private final TrustManagerFactory trustManagerFactory;

    @SneakyThrows
    public String callServerTls() {
        SSLContext sslContext = createSslContext();

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(new URI("https://localhost:8080/api/tls-only"))
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return "Response code: " + response.statusCode();
        }
        return response.body();
    }

    @SneakyThrows
    private SSLContext createSslContext() {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(
                new KeyManager[] {keyManagerFactory.getKeyManagers()[0]} ,
                new TrustManager[] {trustManagerFactory.getTrustManagers()[0]},
                new SecureRandom()
        );
        return sslContext;
    }
}
