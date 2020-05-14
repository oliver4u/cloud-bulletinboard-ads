package com.sap.bulletinboard.ads.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Advertisement {
    @NotBlank
    private String title;
}
