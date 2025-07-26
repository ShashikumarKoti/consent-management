package com.example.consent.consent.service;

import com.example.consent.consent.model.ConsentRequest;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class CreateConsentService {

    Map<String,String> jwsMap = new HashMap<>();

    @Autowired
    private KeyPair keyPairGenerator;

    public String generateJWSConsent(ConsentRequest consentRequest) throws JOSEException, NoSuchAlgorithmException {
        // Create JWS header

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPairGenerator.getPrivate();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();

        // Create JWS object with payload
        JWSObject jwsObject = new JWSObject(header, new Payload(consentRequest.toString()));

        // Sign the JWS
        JWSSigner signer = new RSASSASigner(privateKey);
        jwsObject.sign(signer);

        // Get compact serialization and remove payload (detached)
        String[] parts = jwsObject.serialize().split("\\.");
        String detachedJws =  parts[0] + ".." + parts[2];
        jwsMap.put("detachedJws", detachedJws);
        return detachedJws;
    }


    public boolean verifyJWSConsent(ConsentRequest consentRequest) throws ParseException, JOSEException, NoSuchAlgorithmException, java.text.ParseException {
        // Split the detached JWS into parts
        String[] parts = this.jwsMap.get("detachedJws").split("\\.\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid JWS format");
        }

        // Reconstruct the JWS with the payload
        String reconstructedJWS = parts[0] + "." + Base64URL.encode(consentRequest.toString()) + "." + parts[1];

        // Parse the JWS
        JWSObject jwsObject = JWSObject.parse(reconstructedJWS);

        // Verify the JWS
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) keyPairGenerator.getPublic());
        return jwsObject.verify(verifier);
    }

}
