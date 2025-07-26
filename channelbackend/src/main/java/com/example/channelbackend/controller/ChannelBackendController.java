package com.example.channelbackend.controller;

import com.example.channelbackend.model.OmniChannelRequest;
import com.example.channelbackend.service.ChannelBackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/channel-backend")
public class ChannelBackendController {

    @Autowired
    private ChannelBackendService channelBackendService;
    @PostMapping("/status")
    public String callOmniChannel(@RequestBody OmniChannelRequest omniChannelRequest) {
        String channelBackendStatus = channelBackendService.callOmniChannelService(omniChannelRequest);
        System.out.println("channelBackend " + channelBackendStatus);
        return channelBackendStatus;
    }
}
