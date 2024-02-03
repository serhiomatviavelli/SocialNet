package ru.socialnet.team43.repository.mapper;

import org.mapstruct.Mapper;
import jooq.db.tables.records.CityRecord;
import org.mapstruct.Mapping;
import ru.socialnet.team43.dto.geo.CityDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CityMapper {

    List<CityRecord> mapToList(List<CityDto> cityDtoList);

    @Mapping(target = "id", ignore = true)
    CityRecord cityDtoToCityRecord(CityDto cityDto);
}
