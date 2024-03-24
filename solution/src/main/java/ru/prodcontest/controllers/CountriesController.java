package ru.prodcontest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.prodcontest.models.CountriesModel;
import ru.prodcontest.repositories.CountriesRepositories;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountriesController {
    private final CountriesRepositories countriesRepositories;

    @GetMapping
    public ResponseEntity<List<CountriesModel>> countries(@RequestParam(required = false) List<String> region){
        List<CountriesModel> countryList = new ArrayList<>();

        if(region != null){
            for(int i = 0; i < region.size(); i++){
                List<CountriesModel> countriesInRegion = countriesRepositories.findAllByRegion(region.get(i));
                countryList.addAll(countriesInRegion);
            }
        } else {
            countryList = countriesRepositories.findAll();
        }

        return new ResponseEntity<>(countryList, HttpStatus.OK);
    }

    @GetMapping("/{alpha2}")
    public ResponseEntity<CountriesModel> getCountryByAlpha2(@PathVariable String alpha2) {
        CountriesModel country = countriesRepositories.findByAlpha2(alpha2.toUpperCase());
        if(country != null) {
            return new ResponseEntity<>(country, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
