package com.example.consent.consent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class ConsentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsentApplication.class, args);
	}

	@Bean
	public KeyPair keyPairGenerator() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		KeyPair keyPair = keyGen.generateKeyPair();
		return keyPair;
	}
}
