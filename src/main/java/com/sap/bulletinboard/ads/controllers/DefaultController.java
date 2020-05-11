package com.sap.bulletinboard.ads.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
@RestController
public class DefaultController {

    @GetMapping
    public String get() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cloud Application:\n");
        stringBuilder.append("Bulletinboard-ads\n");
        stringBuilder.append("Oliver's exercise.");
        return stringBuilder.toString();
    }
}