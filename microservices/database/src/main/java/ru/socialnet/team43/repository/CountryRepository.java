package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.CountryRecord;

import jooq.db.Tables;
import jooq.db.tables.records.CountryRecord;
import lombok.AllArgsConstructor;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static org.jooq.impl.DSL.lower;

@Repository
@AllArgsConstructor
public class CountryRepository {

    private DSLContext context;

    public List<CountryRecord> getCountries() {
        return context.selectFrom(Tables.COUNTRY)
                .where(Tables.COUNTRY.IS_DELETED.eq(false))
                .fetchInto(CountryRecord.class);
    }

    public void truncateCountry() {
        context.truncate(Tables.COUNTRY).execute();
    }

    public void insertCountries(List<CountryRecord> countries) {
        context.batchInsert(countries).execute();
    }

    public Long getCountCountries() {
        return context.selectCount().from(Tables.COUNTRY).fetchOne(0, Long.class);
    }

    public List<String> getCountriesTitlesByPossibleTitles(Set<String> possibleTitles)
            throws Exception {

        List<String> countriesTitles =
                context.select(Tables.COUNTRY.TITLE)
                        .from(Tables.COUNTRY)
                        .where(lower(Tables.COUNTRY.TITLE).in(possibleTitles))
                        .fetchInto(String.class);
        return countriesTitles;
    }
}
