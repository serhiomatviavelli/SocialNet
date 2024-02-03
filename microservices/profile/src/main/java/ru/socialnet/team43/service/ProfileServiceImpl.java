package ru.socialnet.team43.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.service.geo.GeoService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private static final String EMAIL_REGEX = "^(.+)@(\\S+)$";
    private final DatabaseClient databaseClient;
    private final GeoService geoService;

    @Override
    public ResponseEntity<Page<PersonDto>> proceedSearch(
            String author,
            String firstName,
            String lastName,
            String city,
            String country,
            Boolean isDeleted,
            String statusCode,
            Integer ageTo,
            Integer ageFrom,
            String ids,
            String userName,
            Pageable pageable)
            throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<Page<PersonDto>> response;

        AccountSearchDto.AccountSearchDtoBuilder accountSearchDtoBuilder =
                AccountSearchDto.builder();

        if (StringUtils.hasText(statusCode)) {
            accountSearchDtoBuilder.statusCode(statusCode);
        }

        boolean isDtoGenerated =
                generateSearchDtoFromSeparateFields(
                        accountSearchDtoBuilder,
                        firstName.toLowerCase(),
                        lastName.toLowerCase(),
                        city.toLowerCase(),
                        country.toLowerCase(),
                        isDeleted,
                        ageTo,
                        ageFrom,
                        ids);

        if (!isDtoGenerated) {
            generateSearchDtoFromSingleField(
                    accountSearchDtoBuilder, author.toLowerCase(), isDeleted);
        }

        AccountSearchDto accountSearchDto = accountSearchDtoBuilder.build();

        if (accountSearchDto != null) {
            response =
                    databaseClient.getAccountsSearchResult(
                            userName, mapper.writeValueAsString(accountSearchDto), pageable);
        } else {
            response = ResponseEntity.ok(new PageImpl<>(Collections.emptyList()));
        }

        return response;
    }

    private boolean generateSearchDtoFromSeparateFields(
            AccountSearchDto.AccountSearchDtoBuilder dtoBuilder,
            String firstName,
            String lastName,
            String city,
            String country,
            Boolean isDeleted,
            Integer ageTo,
            Integer ageFrom,
            String ids) {
        boolean isDtoGenerated = false;

        dtoBuilder.isDeleted(isDeleted);

        if (StringUtils.hasText(firstName)) {
            dtoBuilder.firstName(Collections.singleton(firstName));
            isDtoGenerated = true;
        }

        if (StringUtils.hasText(lastName)) {
            dtoBuilder.lastName(Collections.singleton(lastName));
            isDtoGenerated = true;
        }

        if (StringUtils.hasText(city)) {
            dtoBuilder.city(Collections.singleton(city));
            isDtoGenerated = true;
        }

        if (StringUtils.hasText(country)) {
            dtoBuilder.country(Collections.singleton(country));
            isDtoGenerated = true;
        }

        if (ageFrom != 0) {
            dtoBuilder.ageFrom(ageFrom);
            isDtoGenerated = true;
        }

        if (ageTo != 0) {
            dtoBuilder.ageTo(ageTo);
            isDtoGenerated = true;
        }

        if (StringUtils.hasText(ids)) {
            Set<String> idsStrSet =
                    Arrays.stream(ids.split("\\s*,\\s*")).collect(Collectors.toSet());
            Set<Long> idsSet = segregateLongValues(idsStrSet);
            if (!idsSet.isEmpty()) {
                dtoBuilder.ids(idsSet);
                isDtoGenerated = true;
            }
        }

        return isDtoGenerated;
    }

    private void generateSearchDtoFromSingleField(
            AccountSearchDto.AccountSearchDtoBuilder dtoBuilder, String author, Boolean isDeleted)
            throws Exception {

        dtoBuilder.isDeleted(isDeleted);

        if (!StringUtils.hasText(author)) {
            return;
        }

        String[] searchStringFragmentsArr = author.split("\\s+");
        Set<String> searchStringFragments = new HashSet<>();
        Collections.addAll(searchStringFragments, searchStringFragmentsArr);

        Set<String> possibleEmails =
                searchStringFragments.stream()
                        .filter(fragment -> fragment.matches(EMAIL_REGEX))
                        .collect(Collectors.toSet());

        if (!possibleEmails.isEmpty()) {
            dtoBuilder.author(possibleEmails);
        } else {
            Set<Long> longValues = segregateLongValues(searchStringFragments);
            fillSearchedAge(dtoBuilder, longValues);
            fillSearchedLocations(dtoBuilder, searchStringFragments);
            dtoBuilder.firstName(searchStringFragments);
            dtoBuilder.lastName(searchStringFragments);
        }
    }

    private void fillSearchedLocations(
            AccountSearchDto.AccountSearchDtoBuilder dtoBuilder, Set<String> searchStringFragments)
            throws Exception {
        List<String> requestedCountries =
                geoService.getCountriesTitlesByPossibleTitles(searchStringFragments);

        if (requestedCountries != null && !requestedCountries.isEmpty()) {
            dtoBuilder.country(
                    requestedCountries.stream()
                            .map(city -> city.toLowerCase())
                            .collect(Collectors.toSet()));
        }

        List<String> requestedCities =
                geoService.getCitiesTitlesByPossibleTitles(searchStringFragments);
        if (requestedCities != null && !requestedCities.isEmpty()) {
            dtoBuilder.city(
                    requestedCities.stream()
                            .map(city -> city.toLowerCase())
                            .collect(Collectors.toSet()));
        }
    }

    private void fillSearchedAge(
            AccountSearchDto.AccountSearchDtoBuilder dtoBuilder, Set<Long> longValues) {
        if (!longValues.isEmpty()) {
            long age =
                    longValues.stream()
                            .filter(intVal -> intVal > 0 && intVal < 150)
                            .findFirst()
                            .get();
            if (age != 0) {
                dtoBuilder.ageFrom((int) age - 1);
                dtoBuilder.ageTo((int) age + 1);
            }
        }
    }

    private Set<Long> segregateLongValues(Set<String> searchStringFragments) {
        Map<String, Long> longValues = new HashMap<>();

        for (String fragment : searchStringFragments) {
            try {
                Long longVal = Long.parseLong(fragment);
                longValues.put(fragment, longVal);
            } catch (NumberFormatException ex) {
                log.info("\"" + fragment + "\" is not a number");
            }
        }

        searchStringFragments.removeAll(longValues.keySet());

        return new HashSet<>(longValues.values());
    }
}
