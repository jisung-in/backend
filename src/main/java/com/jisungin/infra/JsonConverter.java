package com.jisungin.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonConverter {

    private final ObjectMapper om;

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return om.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    public String toJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

}
