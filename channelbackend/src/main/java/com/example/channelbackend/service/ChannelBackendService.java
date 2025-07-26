package com.example.channelbackend.service;

import com.example.channelbackend.model.ConsentRequest;
import com.example.channelbackend.model.ConsentResponseObject;
import com.example.channelbackend.model.OmniChannelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChannelBackendService {

    @Autowired
    private RestTemplate restTemplate;

    public String callOmniChannelService(OmniChannelRequest omniChannelRequest) throws Exception {
        ConsentResponseObject responseObject = callCreateConsent(omniChannelRequest);
        prepareRequestForOmniChannel(omniChannelRequest, responseObject);
        return responseObject.getDetachedJws();
    }

    private ConsentResponseObject callCreateConsent(OmniChannelRequest omniChannelRequest) throws Exception {
        System.out.println("Calling create consent with OmniChannelRequest: " + omniChannelRequest);
        ConsentResponseObject response;
        try {
            String url = "http://localhost:8080/v1/consent/create";
            ConsentRequest consentRequest = prepareConsentRequest(omniChannelRequest);
            response = restTemplate.postForObject(url, consentRequest, ConsentResponseObject.class);

        }catch (Exception e){
            throw new Exception("Error while calling create consent service: " + e.getMessage(), e);
        }
        System.out.println("detachedJws in doFilter of channelBackend = " + response);
        return response;
    }

    private void prepareRequestForOmniChannel(OmniChannelRequest omniChannelRequest, ConsentResponseObject consentResponseObject) {
        String responseObject;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-detached-jws", consentResponseObject.getDetachedJws());
            headers.set("x-public-key", consentResponseObject.getPublicKey());

            HttpEntity<OmniChannelRequest> entity = new HttpEntity<>(omniChannelRequest, headers);
            String url = "http://localhost:8083/v1/omnichannel/create";
            responseObject = restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while calling OmniChannel service: " + e.getMessage(), e);
        }
        System.out.println("Response from OmniChannel service: " + responseObject);
    }

    private ConsentRequest prepareConsentRequest(OmniChannelRequest omniChannelRequest) {
         return new ConsentRequest(omniChannelRequest.getSubject(), omniChannelRequest.getCorrelationId(), omniChannelRequest.getResourceIdentifier());
    }

}