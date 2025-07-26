package com.example.channelbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OmniChannelRequest {

    String subject;
    String correlationId;
    String resourceIdentifier;
    String channelType; // e.g., "email", "sms", "push"
    String messageContent; // Content of the message to be sent
    String timestamp; // Time when the request was made
    String status; // Status of the request (e.g., "pending", "sent",
}
