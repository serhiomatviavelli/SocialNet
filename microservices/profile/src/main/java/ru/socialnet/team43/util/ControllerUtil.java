package ru.socialnet.team43.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ControllerUtil
{
    public <T> ResponseEntity<T> createNewResponseEntity(ResponseEntity<T> inputResponseEntity)
    {
        return ResponseEntity.status(inputResponseEntity.getStatusCode()).body(inputResponseEntity.getBody());
    }
}