package com.example.channelbackend.filter;

import com.example.channelbackend.model.ConsentRequest;
import com.example.channelbackend.util.CachedBodyHttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class RestApiCallFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

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
        String url = "http://localhost:8080/v1/consent/create";
        String detachedJws = restTemplate.postForObject(url, consentRequest, String.class);
        System.out.println("detachedJws in doFilter of channelBackend = " + detachedJws);

        // Continue filter chain
        filterChain.doFilter(wrappedRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
