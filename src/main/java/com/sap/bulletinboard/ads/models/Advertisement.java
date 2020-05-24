package com.sap.bulletinboard.ads.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "advertisement")
@Data
public class Advertisement extends BaseEntity {
    @NotBlank
    private String title;
}
