package ru.socialnet.team43.util;

import feign.FeignException;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.socialnet.team43.client.GeoFeignClient;
import ru.socialnet.team43.service.geo.GeoService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledLoadGeo {

    private final GeoFeignClient geoClient;
    private final GeoService geoService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Value("${geo-check.activated}")
    private boolean activated;

    @Value("${geo-check.initial-delay-sec}")
    private long initialDelaySec;

    @PostConstruct
    private void init() {
        if (!activated) return;

        scheduler.schedule(() -> {
                    log.info("Start checking the CITY and COUNTRY tables.");
                    Boolean isEmpty = null;
                    try {
                        isEmpty = geoClient.checkEmpty();

                    } catch (FeignException ex) {
                        log.warn(ex.getMessage());
                    }

                    if (isEmpty == null) {
                        log.warn("The tables could not be checked: " +
                                "Try starting the database and restarting profile");
                        return;
                    }
                    if (isEmpty) {
                        log.info("Tables CITY and COUNTRY are empty, uploading data.");
                        geoService.load();
                    } else {
                        log.info("The CITY and COUNTRY tables are not empty.");
                    }
                },
                initialDelaySec,
                TimeUnit.SECONDS);

        scheduler.shutdown();
    }

    @Scheduled(cron = "0 0 3 1 * *")
    private void scheduled() {
        log.info("Scheduled: Updating tables COUNTRY and CITY.");
        geoService.load();
    }
}
