package com.purpleit.mclarenchallenge.model;


import com.purpleit.mclarenchallenge.services.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;


/**
 * This class represents the state of the race.
 * It holds and calculates information about the relations between the cars.
 */
@Service
@Slf4j
public class Race {

    @Autowired
    private LocationService locationService;

    private HashMap<Integer, Car> cars = new HashMap<>();
    private ArrayList<Car> orderedCars = new ArrayList<>();


    public Car updateCar(CarCoordinatesDto update) {
        return updateCar(update.getCarIndex(), locationService.getLocation(update.getLocation()), update.getTimestamp());
    }

    public Car updateCar(int carId, Vector2D location, long timestamp) {
        Instant updateTime = Instant.ofEpochMilli(timestamp);
        Car car = cars.get(carId);
        if (car == null) {
            car = new Car(carId, updateTime, location);
            cars.put(carId, car);
            orderedCars.add(car);
        } else {
            car.updateLocation(updateTime, location);
        }

        updatePositions();

        return car;
    }

    // TODO - the return from this can be used to generate race events.
    private Map<Integer, Integer> updatePositions() {
        HashMap<Integer, Integer> changes = new HashMap<>();
        Collections.sort(orderedCars);
        Iterator<Car> it = orderedCars.iterator();
        for(int pos=1; it.hasNext(); pos++) {
            Car car = it.next();
            if(car.getPosition() != pos) {
                changes.put(car.getPosition(), pos);
            }
            car.setPosition(pos);
        }
        return changes;
    }
}
