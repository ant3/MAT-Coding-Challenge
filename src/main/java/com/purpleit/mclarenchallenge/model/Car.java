package com.purpleit.mclarenchallenge.model;


import lombok.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


/**
 * This class represents the state of a car at an instant in time.
 * It also encodes some basic info about the history of the car.
 *
 * This class is not threadsafe.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@ToString
public class Car implements Comparable {

    private static int MILLIS_PER_HOUR = 60 * 60 * 1000;

    // Basic state
    @NonNull
    @EqualsAndHashCode.Include
    private int id;
    @NonNull
    private Instant timestamp;
    @NonNull
    private Vector2D location;
    @NonNull
    private Vector2D velocity;

    // derived info
    private double distTravelled = 0.0;
    @Setter
    private int position = 0;


    public Car(int id, Instant timestamp) {
        this(id, timestamp, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0));
    }

    public Car(int id, Instant timestamp, Vector2D location) {
        this(id, timestamp, location, new Vector2D(0.0, 0.0));
    }

    public double getSpeed() {
        return velocity.getNorm();
    }

    public long getEpochMillis() {
        return (timestamp.getEpochSecond() * 1000) + timestamp.getNano() / 1000000;
    }

    @Synchronized
    public void updateLocation(@NonNull Instant newTimestamp, @NonNull Vector2D newLocation) {
        double timeDiff = ((double)ChronoUnit.MILLIS.between(timestamp, newTimestamp)) / MILLIS_PER_HOUR;
        Vector2D movement = newLocation.subtract(location);

        velocity = movement.scalarMultiply(1.0 / timeDiff);
        distTravelled += movement.getNorm();

        timestamp = newTimestamp;
        location = newLocation;
    }

    @Override
    public int compareTo(Object obj) {
        if(obj instanceof Car) {
            Car other = (Car) obj;
            double distDiff = other.distTravelled - distTravelled;
            if(distDiff == 0.0) {
                return 0;
            } else {
                return (int) Math.round(distDiff / Math.abs(distDiff));
            }
        } else {
            throw new UnsupportedOperationException("Cannot compare a Car to a " + obj.getClass().getName());
        }
    }
}
