package ru.socialnet.team43.controller;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;
import ru.socialnet.team43.service.geo.GeoService;
import ru.socialnet.team43.util.ControllerUtil;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoService geoService;
    private final ControllerUtil controllerUtil;

    @GetMapping("/country")
    public ResponseEntity<List<CountryDto>> getCountry() {
        return ResponseEntity.ok(geoService.getCountry());
    }

    @GetMapping("/country/{countryId}/city")
    public ResponseEntity<List<CityDto>> getCitiesByCountryId(@PathVariable Long countryId) {
        return ResponseEntity.ok(geoService.getCitiesByCountryId(countryId));
    }

    @PutMapping("/load")
    public ResponseEntity<Void> load() {
        log.info("/load");
        return controllerUtil.createNewResponseEntity(geoService.load());
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<Void> handler(FeignException ex) {
        log.warn("Error in the database", ex);
        return ResponseEntity.status(ex.status()).build();
    }
}
