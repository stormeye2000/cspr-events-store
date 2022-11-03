package com.stormeye.event.api.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IngressHealthCheck {

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<String> getHealthCheck(){
        return ResponseEntity.ok("ok");
    }

}
