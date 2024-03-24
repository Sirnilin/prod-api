package ru.prodcontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonInclude;
@Entity
@Table(name = "countries")
public class CountriesModel {
    @Id
    @JsonIgnore
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "alpha2")
    private String alpha2;

    @Column(name = "alpha3")
    private String alpha3;

    @Column(name = "region")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String region;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlpha2() {
        return alpha2;
    }

    public String getAlpha3() {
        return alpha3;
    }

    public String getRegion() {
        return region;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlpha2(String alpha2) {
        this.alpha2 = alpha2;
    }

    public void setAlpha3(String alpha3) {
        this.alpha3 = alpha3;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
