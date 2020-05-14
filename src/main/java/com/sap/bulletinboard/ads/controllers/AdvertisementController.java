package com.sap.bulletinboard.ads.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.bulletinboard.ads.models.Advertisement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(path = AdvertisementController.PATH)
@RestController
@Validated
public class AdvertisementController {
    private static long ID = 0;
    public static final String PATH = "/api/v1/ads";
    private final Map<Long, Advertisement> ads = new HashMap<>();

    @GetMapping
    public AdvertisementList readAll() {
        return new AdvertisementList(ads.values());
    }

    @GetMapping("/{id}")
    public Advertisement readById(@PathVariable("id") @Min(0) Long id) {
        throwIfNonExisting(id);
        return ads.get(id);
    }

    @PostMapping
    public ResponseEntity<Advertisement> add(@RequestBody @Valid Advertisement advertisement, UriComponentsBuilder uriComponentsBuilder) {
        long id = ID++;
        ads.put(id, advertisement);

        URI locationURI = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(locationURI).body(advertisement);
    }

    @PutMapping("/{id}")
    public Advertisement update(@PathVariable("id") Long id, @RequestBody Advertisement advertisement) {
        throwIfNonExisting(id);
        ads.put(id, advertisement);
        return advertisement;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        ads.clear();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        throwIfNonExisting(id);
        ads.remove(id);
    }

    private void throwIfNonExisting(Long id) {
        if (!ads.containsKey(id)) {
            throw new NotFoundException("Advertisement " + id + " not found");
        }
    }

    public static class AdvertisementList {
        @JsonProperty
        private List<Advertisement> advertisements = new ArrayList<>();

        public AdvertisementList(Iterable<Advertisement> ads) {
            ads.forEach(advertisements::add);
        }
    }
}
