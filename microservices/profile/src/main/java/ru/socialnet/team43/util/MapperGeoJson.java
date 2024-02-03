package ru.socialnet.team43.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CountryDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapperGeoJson {

    public List<CountryDto> getCountryList(String jsonArray) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode arrayAreas = mapper.readTree(jsonArray);
        List<CountryDto> countryDtoList = new ArrayList<>();

        for (JsonNode areas : arrayAreas) {
            if (areas.get("name").asText().equals("Другие регионы")) {
                countryDtoList.addAll(getOtherCountriesList(areas.get("areas")));
            } else {
                countryDtoList.add(getCountry(areas));
            }
        }

        return countryDtoList;
    }

    private List<CountryDto> getOtherCountriesList(JsonNode countries) {
        List<CountryDto> countryDtoList = new ArrayList<>();
        for (JsonNode country : countries) {
            if (country.get("name").asText().equals("Другое")) continue;
            countryDtoList.add(getCountry(country));
        }
        return countryDtoList;
    }

    private CountryDto getCountry(JsonNode country) {
        Long id = country.get("id").asLong();
        String title = country.get("name").asText();
        List<CityDto> cities = new ArrayList<>(getCityList(country.get("areas"), country));
        return new CountryDto(id, false, title, cities);
    }

    // areas в данном случае может быть список регионов, областей так и городов
    // Проходим по дереву и находим только города.
    private List<CityDto> getCityList(JsonNode arrayAreas, JsonNode country) {
        List<CityDto> cityDtoList = new ArrayList<>();
        Long countryId = country.get("id").asLong();

        if (arrayAreas.isEmpty()) {
            // Если городов и регионов нет.
            CityDto cityDto =
                    CityDto.builder()
                            .title("Город в " + country.get("name").asText())
                            .isDeleted(false)
                            .countryId(countryId)
                            .build();
            cityDtoList.add(cityDto);
            return cityDtoList;
        }
        for (JsonNode areas : arrayAreas) {
            if (!areas.get("areas").isEmpty()) {
                cityDtoList.addAll(getCityList(areas.get("areas"), country));
            } else {
                String title = areas.get("name").asText();
                CityDto cityDto =
                        CityDto.builder()
                                .isDeleted(false)
                                .title(title)
                                .countryId(countryId)
                                .build();
                cityDtoList.add(cityDto);
            }
        }
        return cityDtoList;
    }
}
