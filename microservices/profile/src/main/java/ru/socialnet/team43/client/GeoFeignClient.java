package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;

import java.util.List;

@FeignClient(name = "geo", url = "${database.url}" + "/geo")
public interface GeoFeignClient {

    @GetMapping("/country")
    List<CountryDto> getAllCountry();

    @GetMapping("/country/{countryId}/city")
    List<CityDto> getCitiesByCountryId(@PathVariable Long countryId);

    @PutMapping("/load")
    ResponseEntity<Void> load(@RequestBody List<CountryDto> countries);

    @GetMapping("/load/check")
    boolean checkEmpty();

    @GetMapping("/countriesTitlesByPossibleTitles")
    List<String> getCountriesTitlesByPossibleTitles(
            @RequestParam("possibleTitles") String possibleTitles);

    @GetMapping("/citiesTitlesByPossibleTitles")
    List<String> getCitiesTitlesByPossibleTitles(
            @RequestParam("possibleTitles") String possibleTitles);
}
