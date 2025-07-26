package com.example.consent.consent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatusCode;

import java.security.KeyPair;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConsentResponseObject {

    String detachedJws;
    String publicKey;
}
