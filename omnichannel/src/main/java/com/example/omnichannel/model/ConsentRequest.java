package com.example.omnichannel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConsentRequest {

    String subject;
    String correlationId;
    String resourceIdentifier;
}
