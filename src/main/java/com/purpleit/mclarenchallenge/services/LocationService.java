package com.purpleit.mclarenchallenge.services;


import com.purpleit.mclarenchallenge.model.CarCoordinatesDto.LatLongLocationDto;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * This class converts from Lat Long to an offset in miles from an arbitrary origin.
 * The origing is simply the first data point it sees.
 * The conversion to miles only works on a small scale for places in Southern England.
 *
 * This class should really use a generic calculation to do the conversion,
 * However, I have just hacked it due to time constraints.
 *
 * This class converts from BigDecimal (>15 SF of precision) to doubles (fast maths).
 */
@Service
public class LocationService {

    // These values are a hack and only work for a latitude of about 52 degrees.
    private static final double MILES_PER_LAT = 69.0;
    private static final double MILES_PER_LONG = 43.0;

    private BigDecimal xOrigin;
    private BigDecimal yOrigin;

    // Returns an offset from the (arbitrary) origin in miles
    public Vector2D getLocation(LatLongLocationDto dto) {
        if(xOrigin == null) {
            xOrigin = dto.getLongitude();
            yOrigin = dto.getLatitude();
        }
        return new Vector2D(dto.getLongitude().subtract(xOrigin).doubleValue() * MILES_PER_LONG,
                dto.getLatitude().subtract(yOrigin).doubleValue() * MILES_PER_LAT);
    }
}
