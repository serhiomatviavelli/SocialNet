package ru.socialnet.team43.service.geo;

import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;

import java.util.List;
import java.util.Set;

public interface GeoServiceDB {
    List<CountryDto> getCountries();

    List<CityDto> getCitiesByCountryId(Long countryId);

    boolean load(List<CountryDto> countries);

    boolean checkEmpty();

    List<String> getCountriesTitlesByPossibleTitles(Set<String> possibleTitles) throws Exception;

    List<String> getCitiesTitlesByPossibleTitles(Set<String> possibleTitles) throws Exception;
}
