package com.example.channelbackend.service;

import com.example.channelbackend.model.ConsentRequest;
import com.example.channelbackend.model.OmniChannelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChannelBackendService {

    @Autowired
    private RestTemplate restTemplate;

    public String callOmniChannelService(OmniChannelRequest omniChannelRequest) {
        //omniChannelRequest.setSubject("123");
         String url = "http://localhost:8083/v1/omnichannel/create";
         return restTemplate.postForObject(url, omniChannelRequest, String.class);
    }

}