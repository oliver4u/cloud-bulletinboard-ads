package com.sap.bulletinboard.ads.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.bulletinboard.ads.models.Advertisement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(path = AdvertisementController.PATH)
@RestController
public class AdvertisementController {
    public static final String PATH = "/api/v1/ads";
    private final Map<Long, Advertisement> ads = new HashMap<>();

    @GetMapping
    public AdvertisementList advertisements() {
        return new AdvertisementList(ads.values());
    }

    @GetMapping("/{id}")
    public Advertisement advertisementById(@PathVariable("id") Long id) {
        if (!ads.containsKey(id)) {
            throw new NotFoundException("Advertisement " + id + " not found");
        }
        return ads.get(id);
    }

    @PostMapping
    public ResponseEntity<Advertisement> add(@RequestBody Advertisement advertisement, UriComponentsBuilder uriComponentsBuilder) {
        long id = ads.size();
        ads.put(id, advertisement);

        URI locationURI = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(locationURI).body(advertisement);
    }

    public static class AdvertisementList {
        @JsonProperty
        private List<Advertisement> advertisements = new ArrayList<>();

        public AdvertisementList(Iterable<Advertisement> ads) {
            ads.forEach(advertisements::add);
        }
    }
}
