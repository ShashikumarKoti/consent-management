package com.example.consent.consent.service;

import com.example.consent.consent.model.ConsentRequest;
import com.example.consent.consent.model.ConsentResponseObject;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CreateConsentService {

    @Autowired
    private KeyPair keyPairGenerator;

    public ConsentResponseObject generateJWSConsent(ConsentRequest consentRequest) throws JOSEException, NoSuchAlgorithmException {

        long now = Instant.now().getEpochSecond();
        long nbf = now;
        long exp = now + 1;

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPairGenerator.getPrivate();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).contentType("application/json")
                .customParam("nbf", nbf)
                .customParam("exp", exp)
                .build();

        // Create JWS object with payload
        JWSObject jwsObject = new JWSObject(header, new Payload(consentRequest.toString()));

        // Sign the JWS
        JWSSigner signer = new RSASSASigner(privateKey);
        jwsObject.sign(signer);

        // Get compact serialization and remove payload (detached)
        String[] parts = jwsObject.serialize().split("\\.");
        String detachedJWS = parts[0] + ".." + parts[2];
        String publicKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPublic().getEncoded());
        return new ConsentResponseObject(detachedJWS,publicKey);
    }
}


//    public boolean verifyJWSConsent(ConsentRequest consentRequest) throws ParseException, JOSEException, NoSuchAlgorithmException, java.text.ParseException {
//
//        // Simulate a delay to mimic real-world scenarios
////        try {
////            Thread.sleep(1100);
////        } catch (InterruptedException e) {
////            throw new RuntimeException(e);
////        }
//
//        String[] parts = this.jwsMap.get("detachedJws").split("\\.\\.");
//        if (parts.length != 2) {
//            throw new IllegalArgumentException("Invalid JWS format");
//        }
//
//        // Reconstruct the JWS with the payload
//        String reconstructedJWS = parts[0] + "." + Base64URL.encode(consentRequest.toString()) + "." + parts[1];
//
//        // Parse the JWS
//        JWSObject jwsObject = JWSObject.parse(reconstructedJWS);
//
//        // Verify the JWS
//        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) keyPairGenerator.getPublic());
//        boolean verified = jwsObject.verify(verifier);
//
//        if (verified) {
//            //check nbf and exp
//            long now = Instant.now().getEpochSecond();
//            System.out.println("now in consent service = " + now);
//            long nbf = (long) jwsObject.getHeader().getCustomParam("nbf");
//            System.out.println("nbf in consent service = " + nbf);
//            long exp = (long) jwsObject.getHeader().getCustomParam("exp");
//            System.out.println("exp in consent service = " + exp);
//
//            if (now < nbf || now > exp) {
//                throw new JOSEException("JWS is not valid at this time");
//            } else {
//                return verified;
//            }
//        }
//
//        return verified;
//    }
//}
