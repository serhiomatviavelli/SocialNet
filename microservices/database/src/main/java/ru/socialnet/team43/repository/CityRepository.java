package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.Tables;
import jooq.db.tables.records.CityRecord;

import lombok.AllArgsConstructor;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import ru.socialnet.team43.dto.geo.CityDto;
import ru.socialnet.team43.dto.geo.CityDto;

import java.util.List;
import java.util.Set;

import static org.jooq.impl.DSL.lower;

@Repository
@AllArgsConstructor
public class CityRepository {

    private DSLContext context;

    public List<CityDto> getCitiesByCountriesId(List<Long> countriesId) {
        return context.selectFrom(Tables.CITY)
                .where(Tables.CITY.COUNTRY_ID.in(countriesId))
                .and(Tables.CITY.IS_DELETED.eq(false))
                .fetchInto(CityDto.class);
    }

    public List<CityDto> getCitiesByCountryId(Long countryId) {
        return context.selectFrom(Tables.CITY)
                .where(Tables.CITY.COUNTRY_ID.eq(countryId))
                .and(Tables.CITY.IS_DELETED.eq(false))
                .fetchInto(CityDto.class);
    }

    public void truncateCity() {
        context.truncate(Tables.CITY).execute();
    }

    public void insertCities(List<CityRecord> cities) {
        context.batchInsert(cities).execute();
    }

    public Long getCountCities() {
        return context.selectCount().from(Tables.CITY).fetchOne(0, Long.class);
    }
    public List<String> getCitiesTitlesByPossibleTitles(Set<String> possibleTitles)
            throws Exception {
        List<String> citiesTitles =
                context.select(Tables.CITY.TITLE)
                        .from(Tables.CITY)
                        .where(lower(Tables.CITY.TITLE).in(possibleTitles))
                        .fetchInto(String.class);
        return citiesTitles;
    }
}
