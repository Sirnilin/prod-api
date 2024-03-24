package ru.prodcontest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prodcontest.models.CountriesModel;

import java.util.List;

@Repository
public interface CountriesRepositories extends JpaRepository<CountriesModel, Long> {
    List<CountriesModel> findAllByRegion(String region);
    CountriesModel findByAlpha2(String alpha2);
}
