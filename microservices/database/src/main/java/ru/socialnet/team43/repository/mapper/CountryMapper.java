package ru.socialnet.team43.repository.mapper;

import org.mapstruct.Mapper;
import jooq.db.tables.records.CountryRecord;
import ru.socialnet.team43.dto.geo.CountryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryRecord map(CountryDto countryDto);
    List<CountryRecord> mapToList(List<CountryDto> countryDto);
}
