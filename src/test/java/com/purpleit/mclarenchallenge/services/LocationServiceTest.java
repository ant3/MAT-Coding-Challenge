package com.purpleit.mclarenchallenge.services;


import com.purpleit.mclarenchallenge.model.CarCoordinatesDto.LatLongLocationDto;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class LocationServiceTest {

    @Test
    public void getLocation() {
        LocationService service = new LocationService();

        // First location is always 0,0
        assertThat(service.getLocation(new LatLongLocationDto(new BigDecimal("52.0"), new BigDecimal("0.0"))),
                equalTo(new Vector2D(0, 0)));
        // Subsequent locations are offsets from first.
        assertThat(service.getLocation(new LatLongLocationDto(new BigDecimal("52.000002"), new BigDecimal("0.00000001"))),
                equalTo(new Vector2D(0.00000043, 0.000138)));
    }
}
