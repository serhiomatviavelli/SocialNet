package ru.socialnet.team43.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ControllerUtil
{
    private static ObjectMapper mapper = new ObjectMapper();

    public <T> T stringToObject(String objectString, Class<T> objectClass) throws Exception
    {
        T object = mapper.readValue(objectString, objectClass);

        return object;
    }
}
