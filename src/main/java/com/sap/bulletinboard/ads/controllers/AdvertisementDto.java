package com.sap.bulletinboard.ads.controllers;

import com.sap.bulletinboard.ads.models.Advertisement;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class AdvertisementDto {
    private Long id;

    @NotBlank
    private String title;

    private MetaData metadata = new MetaData();

    public AdvertisementDto(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.metadata.createdAt = convertToDateTime(ad.getCreatedAt());
        this.metadata.updatedAt = convertToDateTime(ad.getUpdatedAt());
        this.metadata.version = ad.getVersion();
    }

    public Advertisement toEntity() {
        Advertisement ad = new Advertisement();
        ad.setId(this.id);
        ad.setTitle(this.title);
        ad.setVersion(this.metadata.version);
        return ad;
    }

    private String convertToDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // ISO 8601
    }

    @Data
    private static class MetaData {
        private String createdAt;
        private String updatedAt;
        private long version;
    }
}
