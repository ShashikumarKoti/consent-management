package com.example.consent.verifier.filter;

import com.example.consent.verifier.CachedBodyHttpServletRequest;
import com.example.consent.verifier.model.ConsentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

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

        String body = new String(wrappedRequest.getInputStream().readAllBytes(), servletRequest.getCharacterEncoding());
        JsonNode jsonNode = objectMapper.readTree(body);

        String subject = jsonNode.path("subject").asText();
        String correlationId = jsonNode.path("correlationId").asText();
        String resourceIdentifier = jsonNode.path("resourceIdentifier").asText();
        ConsentRequest consentRequest = new ConsentRequest(subject, correlationId, resourceIdentifier);

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/v1/consent/verify";
        boolean s = Boolean.TRUE.equals(restTemplate.postForObject(url, consentRequest, Boolean.class));
        if(s) {
            System.out.println("Consent verified successfully for request: " + consentRequest);
        } else {
            throw new Exception("Consent verification failed for request: " + consentRequest);
        }

        // Continue filter chain
        filterChain.doFilter(wrappedRequest, servletResponse);
    }

    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
