package ru.socialnet.team43.service.geo;

import org.springframework.http.ResponseEntity;
import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;

import java.util.List;
import java.util.Set;

public interface GeoService {
    List<CountryDto> getCountry();

    List<CityDto> getCitiesByCountryId(Long countryId);

    ResponseEntity<Void> load();

    List<String> getCountriesTitlesByPossibleTitles(Set<String> possibleTitles) throws Exception;

    List<String> getCitiesTitlesByPossibleTitles(Set<String> possibleTitles) throws Exception;
}
