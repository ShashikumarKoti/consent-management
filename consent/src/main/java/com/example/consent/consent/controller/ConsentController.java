package com.example.consent.consent.controller;

import com.example.consent.consent.model.ConsentRequest;
import com.example.consent.consent.service.CreateConsentService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@RestController
@RequestMapping("v1/consent")
public class ConsentController {

    @Autowired
    private CreateConsentService createConsentService;

    @PostMapping("/create")
    public String createConsent(@RequestBody ConsentRequest  consentRequest) throws JOSEException, NoSuchAlgorithmException {
        String detachedJws = createConsentService.generateJWSConsent(consentRequest);
        System.out.println("detachedJws in consent= " + detachedJws);
        return detachedJws;
    }

    @PostMapping("/verify")
    public boolean verifyConsent(@RequestBody ConsentRequest consentRequest) throws NoSuchAlgorithmException, ParseException, JOSEException {
        boolean consent = createConsentService.verifyJWSConsent(consentRequest);
        System.out.println("Verifying consent for request: " + consent);
        return consent;

    }
}
