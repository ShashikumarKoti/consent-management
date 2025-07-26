package com.example.channelbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.security.KeyPair;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConsentResponseObject {

    String detachedJws;
    String publicKey;
}
