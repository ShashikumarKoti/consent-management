package com.example.consent.verifier.filter;

import com.example.consent.verifier.CachedBodyHttpServletRequest;
import com.example.consent.verifier.model.ConsentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RestApiCallFilter implements Filter  {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // Example REST API call
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);

        String detachedJws = httpRequest.getHeader("x-detached-jws");
        // Decode the public key from the header
        String keyString = httpRequest.getHeader("x-public-key");
        byte[] decoded = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);

        String body = new String(wrappedRequest.getInputStream().readAllBytes(), servletRequest.getCharacterEncoding());
        JsonNode jsonNode = objectMapper.readTree(body);

        String subject = jsonNode.path("subject").asText();
        String correlationId = jsonNode.path("correlationId").asText();
        String resourceIdentifier = jsonNode.path("resourceIdentifier").asText();
        ConsentRequest consentRequest = new ConsentRequest(subject, correlationId, resourceIdentifier);

        String[] parts = detachedJws.split("\\.\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid JWS format");
        }
        // Reconstruct the JWS with the payload
        String reconstructedJWS = parts[0] + "." + Base64URL.encode(consentRequest.toString()) + "." + parts[1];

        // Parse the JWS
        JWSObject jwsObject = JWSObject.parse(reconstructedJWS);
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        boolean isValid = jwsObject.verify(verifier);
        System.out.println("Is JWS valid? " + isValid);
        if( isValid) {
            // Check nbf and exp
            long now = System.currentTimeMillis() / 1000;
            System.out.println("now in consent service = " + now);
            long nbf = (long) jwsObject.getHeader().getCustomParam("nbf");
            System.out.println("nbf in consent service = " + nbf);
            long exp = (long) jwsObject.getHeader().getCustomParam("exp");
            System.out.println("exp in consent service = " + exp);

            if (now < nbf || now > exp) {
                throw new IOException("JWS is not valid at this time");
            }
        } else {
            throw new IOException("JWS verification failed");
    }
        // Continue filter chain
        filterChain.doFilter(wrappedRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
