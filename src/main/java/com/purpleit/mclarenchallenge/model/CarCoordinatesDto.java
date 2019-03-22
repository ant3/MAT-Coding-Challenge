package com.purpleit.mclarenchallenge.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


/**
 * This class represents the coordinates messages coming into the system
 */
@Data
@AllArgsConstructor
public class CarCoordinatesDto {
    int carIndex;
    LatLongLocationDto location;
    long timestamp;

    /**
     * This class represents a location expressed in terms of Latitude and Longitude.
     * Note that BigDecmal is used because the input regularly has more than the 15SF that double can deal with.
     */
    @Data
    @AllArgsConstructor
    public static class LatLongLocationDto {
        @JsonProperty("lat")
        BigDecimal latitude;
        @JsonProperty("long")
        BigDecimal longitude;
    }
}
