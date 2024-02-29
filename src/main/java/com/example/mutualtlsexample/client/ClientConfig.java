package com.example.mutualtlsexample.client;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.SneakyThrows;

@Configuration(proxyBeanMethods = false)
public class ClientConfig {

    private static final String CLIENT_CERT_CHAIN = "misc/tls/client.cer";
    private static final String CLIENT_PK = "misc/tls/client.p8.key";
    private static final char[] CLIENT_PK_PWD = "adminadmin".toCharArray();
    private static final String ROOT_CRT = "misc/tls/root.cer";

    @Bean
    public KeyManagerFactory customClientKeyManagerFactory() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, CLIENT_PK_PWD); // Initialize the keystore

        Key clientKey = loadPK(CLIENT_PK);
        Certificate[] clientCertChain = parseCertificates(CLIENT_CERT_CHAIN);
        keyStore.setKeyEntry("client", clientKey, CLIENT_PK_PWD, clientCertChain);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, CLIENT_PK_PWD);
        return keyManagerFactory;
    }

    @SneakyThrows
    private PrivateKey loadPK(String path) {
        String pkcs8 = Files.readString(Path.of(path)).replaceAll("(-----BEGIN ([A-Z ]+)-----|-----END ([A-Z ]+)-----|\r|\n)", "");
        byte[] decoded = Base64.getDecoder().decode(pkcs8);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private Certificate[] parseCertificates(String path) throws Exception {
        String pemContent = Files.readString(Path.of(path));
        try (InputStream certInputStream = new ByteArrayInputStream(pemContent.getBytes(StandardCharsets.UTF_8))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certs = cf.generateCertificates(certInputStream);

            return certs.toArray(new Certificate[0]);
        }
    }

    @Bean
    public TrustManagerFactory customTrustManagerFactory() throws Exception {
        Certificate rootCertificate;
        try (var rootCertIS = new FileInputStream(Path.of(ROOT_CRT).toFile())) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            rootCertificate = certFactory.generateCertificate(rootCertIS);
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca-certificate", rootCertificate);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }
}
