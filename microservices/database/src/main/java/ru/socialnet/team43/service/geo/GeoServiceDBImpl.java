package ru.socialnet.team43.service.geo;

import jooq.db.tables.records.CityRecord;
import jooq.db.tables.records.CountryRecord;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;
import ru.socialnet.team43.repository.CityRepository;
import ru.socialnet.team43.repository.CountryRepository;
import ru.socialnet.team43.repository.mapper.CityMapper;
import ru.socialnet.team43.repository.mapper.CountryMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GeoServiceDBImpl implements GeoServiceDB {

    private final CountryRepository countryRepo;
    private final CityRepository cityRepo;
    private final CountryMapper countryMapper;
    private final CityMapper cityMapper;

    @Override
    public List<CountryDto> getCountries() {
        List<CountryRecord> countries = countryRepo.getCountries();
        List<CityDto> cities =
                cityRepo.getCitiesByCountriesId(
                        countries.stream().map(CountryRecord::getId).collect(Collectors.toList()));
        List<CountryDto> countryDtoList = new ArrayList<>();

        for (CountryRecord country : countries) {
            CountryDto countryDto =
                    CountryDto.builder()
                            .id(country.getId())
                            .isDeleted(country.getIsDeleted())
                            .title(country.getTitle())
                            .cities(new ArrayList<>())
                            .build();
            countryDtoList.add(countryDto);
        }

        for (CountryDto country : countryDtoList) {
            List<CityDto> cityDtoList = country.getCities();
            for (CityDto city : cities) {
                if (country.getId().equals(city.getCountryId())) {
                    cityDtoList.add(city);
                }
            }
        }

        return countryDtoList;
    }

    @Override
    public List<CityDto> getCitiesByCountryId(Long countryId) {
        return cityRepo.getCitiesByCountryId(countryId);
    }

    public boolean load(List<CountryDto> countries) {
        cityRepo.truncateCity();
        countryRepo.truncateCountry();
        List<CityRecord> cityRecords = new ArrayList<>();
        for (CountryDto country : countries) {
            cityRecords.addAll(cityMapper.mapToList(country.getCities()));
        }
        cityRepo.insertCities(cityRecords);

        List<CountryRecord> countryRecords = countryMapper.mapToList(countries);
        countryRepo.insertCountries(countryRecords);

        return true;
    }

    @Override
    public boolean checkEmpty() {
        return countryRepo.getCountCountries() == 0 || cityRepo.getCountCities() == 0;
    }
    @Override
    public List<String> getCountriesTitlesByPossibleTitles(Set<String> possibleTitles) throws Exception {
        return countryRepo.getCountriesTitlesByPossibleTitles(possibleTitles);
    }

    @Override
    public List<String> getCitiesTitlesByPossibleTitles(Set<String> possibleTitles) throws Exception {
        return cityRepo.getCitiesTitlesByPossibleTitles(possibleTitles);
    }

}
