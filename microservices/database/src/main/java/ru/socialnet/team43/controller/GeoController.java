package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;
import ru.socialnet.team43.service.geo.GeoServiceDB;
import ru.socialnet.team43.util.ControllerUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/geo")
@AllArgsConstructor
public class GeoController {

    private final ControllerUtil controllerUtil;
    private final GeoServiceDB geoService;

    @GetMapping("/country")
    public List<CountryDto> getCountry() {
        log.info("/country");
        return geoService.getCountries();
    }

    @GetMapping("/country/{countryId}/city")
    public List<CityDto> getCitiesByCountryId(@PathVariable Long countryId) {
        log.info("/country/{}/city", countryId);
        return geoService.getCitiesByCountryId(countryId);
    }

    @PutMapping("/load")
    public ResponseEntity<Void> load(@RequestBody List<CountryDto> countries) {
        log.info("/load Trying to insert data to the DB");
        if (geoService.load(countries)) {
            return ResponseEntity.ok().build();
        }
        log.warn("/load Data could not be inserted into the database");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/load/check")
    public boolean checkEmpty() {
        log.info("/load/check");
        return geoService.checkEmpty();
    }

    @GetMapping("/countriesTitlesByPossibleTitles")
    public ResponseEntity<List<String>> getCountriesTitlesByPossibleTitles(
            @RequestParam("possibleTitles") String possibleTitles) {
        List<String> result;

        try {

            Set<String> possibleTitlesSet =
                    (Set<String>) controllerUtil.stringToObject(possibleTitles, Set.class);
            result = geoService.getCountriesTitlesByPossibleTitles(possibleTitlesSet);
        } catch (Exception ex) {
            log.error(GeoController.class.getCanonicalName(), ex);
            result = Collections.emptyList();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/citiesTitlesByPossibleTitles")
    public ResponseEntity<List<String>> getCitiesTitlesByPossibleTitles(
            @RequestParam("possibleTitles") String possibleTitles) {
        List<String> result;

        try {

            Set<String> possibleTitlesSet =
                    (Set<String>) controllerUtil.stringToObject(possibleTitles, Set.class);
            result = geoService.getCitiesTitlesByPossibleTitles(possibleTitlesSet);
        } catch (Exception ex) {
            log.error(GeoController.class.getCanonicalName(), ex);
            result = Collections.emptyList();
        }

        return ResponseEntity.ok(result);
    }
}
