package com.sap.bulletinboard.ads.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequestMapping(path = AdvertisementController.PATH)
@RestController
@Validated
public class AdvertisementController {

    public static final String PATH = "/api/v1/ads";
    public static final String PATH_PAGES = PATH + "/pages/";
    private static final int FIRST_PAGE_ID = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private AdvertisementRepository repository;

    @Autowired
    public AdvertisementController(AdvertisementRepository advertisementRepository) {
        this.repository = advertisementRepository;
    }

    @GetMapping
    public ResponseEntity<AdvertisementDtoList> readAll() {
        return readPage(FIRST_PAGE_ID);
    }

    @GetMapping("/pages/{pageId}")
    public ResponseEntity<AdvertisementDtoList> readPage(@PathVariable("pageId") int pageId) {
        Page<Advertisement> page = repository.findAll(PageRequest.of(pageId, DEFAULT_PAGE_SIZE));

        return new ResponseEntity<AdvertisementDtoList>(new AdvertisementDtoList(page.getContent()),
                buildPageHeader(page, PATH_PAGES), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public AdvertisementDto readById(@PathVariable("id") @Min(0) Long id) {
        throwIfIdNonExisting(id);
        return new AdvertisementDto(repository.findById(id).get());
    }

    @PostMapping
    public ResponseEntity<AdvertisementDto> add(@RequestBody @Valid AdvertisementDto adDto, UriComponentsBuilder uriComponentsBuilder) {
        throwIfIdExisting(adDto.getId());
        AdvertisementDto newAd = new AdvertisementDto(repository.save(adDto.toEntity()));
        Long id = newAd.getId();

        URI locationURI = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(locationURI).body(newAd);
    }

    @PutMapping("/{id}")
    public AdvertisementDto update(@PathVariable("id") Long id, @RequestBody AdvertisementDto adDto) {
        throwIfIdNonExisting(id);
        throwIfIdInconsistent(id, adDto.getId());
        return new AdvertisementDto(repository.save(adDto.toEntity()));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        repository.deleteAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        throwIfIdNonExisting(id);
        repository.deleteById(id);
    }

    private HttpHeaders buildPageHeader(Page<?> page, String path) {
        StringBuilder pageLink = new StringBuilder();

        if (page.hasPrevious()) {
            pageLink.append("<")
                    .append(path)
                    .append(page.getNumber() - 1)
                    .append(">; rel=\"previous\"");
            if (page.hasNext()) {
                pageLink.append(", ");
            }
        }
        if (page.hasNext()) {
            pageLink.append("<")
                    .append(path)
                    .append(page.getNumber() + 1)
                    .append(">; rel=\"next\"");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.LINK, pageLink.toString());

        return httpHeaders;
    }

    private void throwIfIdExisting(final Long id) {
        if (id != null && id.intValue() != 0) {
            throw new BadRequestException(String.format(
                    "Remove 'id' property from request or use PUT method to update resource with id = %d", id));
        }
    }

    private void throwIfIdNonExisting(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Advertisement " + id + " not found");
        }
    }

    private void throwIfIdInconsistent(Long expected, Long actual) {
        if (!expected.equals(actual)) {
            throw new BadRequestException(String.format(
                    "bad request, inconsistent IDs between request and object: request id = %d, object id = %d",
                    expected, actual));
        }
    }

    private static class AdvertisementDtoList {
        @JsonProperty
        private List<AdvertisementDto> advertisements = new ArrayList<>();

        public AdvertisementDtoList(Iterable<Advertisement> ads) {
            ads.forEach(ad -> advertisements.add(new AdvertisementDto(ad)));
        }
    }
}
