package com.purpleit.mclarenchallenge.model;


import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
public class RaceTest {

    @Test
    public void updateCarTest() {
        Race race = new Race();

        Car car0 = race.updateCar(0, new Vector2D(0,0), 0);
        Car car1 = race.updateCar(1, new Vector2D(0,0), 0);

        assertThat(car0.getPosition(), equalTo(1));
        assertThat(car1.getPosition(), equalTo(2));

        race.updateCar(0, new Vector2D(1,0), 1);
        race.updateCar(1, new Vector2D(2,0), 1);

        assertThat(car0.getPosition(), equalTo(2));
        assertThat(car1.getPosition(), equalTo(1));
    }
}
