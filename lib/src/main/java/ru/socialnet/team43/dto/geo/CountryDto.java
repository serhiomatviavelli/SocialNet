package ru.socialnet.team43.dto.geo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    private Long id;
    private Boolean isDeleted;
    private String title;
    private List<CityDto> cities;
}
