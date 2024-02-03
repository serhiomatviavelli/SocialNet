package ru.socialnet.team43.dto.geo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDto {
    private Long id;
    private Boolean isDeleted;
    private String title;
    private Long countryId;
}
