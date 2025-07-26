package com.example.omnichannel.controller;

import com.example.omnichannel.model.OmniChannelRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/omnichannel")
public class OmniChannelController {

     @PostMapping("/create")
     public String getOmniChannelStatus(@RequestBody OmniChannelRequest omniChannelRequest) {
        System.out.println("Received OmniChannelRequest: " + omniChannelRequest);
         return "Omnichannel is operational";
     }
}
