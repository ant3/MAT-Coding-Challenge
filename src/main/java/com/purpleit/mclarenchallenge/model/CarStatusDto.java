package com.purpleit.mclarenchallenge.model;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * This class represents the status updates sent to the visualisation.
 */
@Data
@AllArgsConstructor
public class CarStatusDto {

    long timestamp;
    int carIndex;
    ValueType type;
    double value;

    public static enum ValueType {
        POSITION,
        SPEED
    }
}
