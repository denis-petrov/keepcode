package com.keepcode.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AvailableNumber(
        @JsonProperty("number") String number,
        @JsonProperty("country") int countryId,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,

        @JsonProperty("data_humans") String dataHumans,
        @JsonProperty("full_number") String fullNumber,
        @JsonProperty("country_text") String countryText,

        @JsonProperty("maxdate")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime maxDate,
        @JsonProperty("status") String status
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailableNumber that = (AvailableNumber) o;
        return countryId == that.countryId
                && Objects.equals(number, that.number)
                && Objects.equals(updatedAt, that.updatedAt)
                && Objects.equals(dataHumans, that.dataHumans)
                && Objects.equals(fullNumber, that.fullNumber)
                && Objects.equals(countryText, that.countryText)
                && Objects.equals(maxDate, that.maxDate)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                fullNumber,
                countryId
        );
    }
}
