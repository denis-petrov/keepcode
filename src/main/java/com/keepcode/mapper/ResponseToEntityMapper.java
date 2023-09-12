package com.keepcode.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepcode.entity.AvailableNumber;
import com.keepcode.entity.Country;
import com.keepcode.exception.InvalidApiResponseException;
import com.keepcode.exception.JsonMapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ResponseToEntityMapper {

    private static final Logger log = LoggerFactory.getLogger(ResponseToEntityMapper.class);

    private final ObjectMapper objectMapper;

    public ResponseToEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Country> parseCountries(String jsonResponse) throws JsonMapperException {
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            ensureApiResponseValid(jsonMap);
            ensureCountriesJsonValid(jsonMap);

            return objectMapper.convertValue(jsonMap.get("countries"), new TypeReference<>() {});
        } catch (Exception ex) {
            log.error("Catch exception while parse countries from json. Message #{}", ex.getMessage());
            throw new JsonMapperException(ex.getMessage());
        }
    }

    public List<AvailableNumber> parsePhoneNumbers(String jsonResponse) throws JsonMapperException {
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            ensureApiResponseValid(jsonMap);
            ensurePhoneNumbersJsonValid(jsonMap);

            return objectMapper.convertValue(jsonMap.get("numbers"), new TypeReference<>() {});
        } catch (Exception ex) {
            log.error("Catch exception while parse numbers from json. Message #{}", ex.getMessage());
            throw new JsonMapperException(ex.getMessage());
        }
    }

    private void ensureApiResponseValid(Map<String, Object> jsonMap) {
        if (!jsonMap.containsKey("response")) {
            throw new InvalidApiResponseException("Invalid api response, key 'response' does not exist in json");
        }
        if (!jsonMap.get("response").toString().equals("1")) {
            throw new InvalidApiResponseException(
                    "Invalid api response, key 'response' does not equals valid state, curr state #"
                            + jsonMap.get("response")
            );
        }
    }

    private void ensurePhoneNumbersJsonValid(Map<String, Object> jsonMap) {
        if (!jsonMap.containsKey("numbers")) {
            throw new JsonMapperException("Api response does not contain 'numbers' field.");
        }
    }

    private void ensureCountriesJsonValid(Map<String, Object> jsonMap) {
        if (!jsonMap.containsKey("countries")) {
            throw new JsonMapperException("Api response does not contain 'countries' field.");
        }
    }
}
