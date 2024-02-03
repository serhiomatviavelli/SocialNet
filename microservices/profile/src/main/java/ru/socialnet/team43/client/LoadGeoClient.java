package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "loaderGeo", url = "${loader-geo.url}")
public interface LoadGeoClient {

    @GetMapping("/areas")
    ResponseEntity<String> getData();
}
