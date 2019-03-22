package com.purpleit.mclarenchallenge.model;


import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class CarTest {

    @Test
    public void updateLocationTest() {
        Car car = new Car(0, Instant.ofEpochMilli(100), new Vector2D(0, 0));
        car.updateLocation(Instant.ofEpochMilli(200), new Vector2D(0, 1));

        assertThat(car.getLocation(), equalTo(new Vector2D(0, 1)));
        assertThat(car.getDistTravelled(), equalTo(1.0));
        assertThat(car.getSpeed(), equalTo(36000.0));
    }

    @Test
    public void compareToTest_equal() {
        Car car1 = new Car(0, Instant.ofEpochMilli(100), new Vector2D(0, 0));
        car1.updateLocation(Instant.ofEpochMilli(200), new Vector2D(0, 1));

        Car car2 = new Car(0, Instant.ofEpochMilli(100), new Vector2D(0, 0));
        car2.updateLocation(Instant.ofEpochMilli(200), new Vector2D(0, 1));

        assertThat(car1.compareTo(car2), equalTo(0));
        assertThat(car2.compareTo(car1), equalTo(0));
    }

    @Test
    public void compareToTest_different() {
        Car car1 = new Car(0, Instant.ofEpochMilli(100), new Vector2D(0, 0));
        car1.updateLocation(Instant.ofEpochMilli(200), new Vector2D(0, 1));

        Car car2 = new Car(0, Instant.ofEpochMilli(100), new Vector2D(0, 0));
        car2.updateLocation(Instant.ofEpochMilli(200), new Vector2D(1, 1));

        assertThat(car1.compareTo(car2), equalTo(1));
        assertThat(car2.compareTo(car1), equalTo(-1));
    }
}
