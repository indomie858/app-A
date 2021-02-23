package com.example.appa.viewmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompassViewModel {
    private Double nextStepBearing;
    private Double userBearing;
    private String userDirectionString;

    List<String> directions = Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "NW");

    public void setNextStepBearing(Double nextStepBearing) {
        this.nextStepBearing = nextStepBearing;
    }

    // This function assumes that the input is from the geomagnetic sensor,
    // which uses 0 as North, negative 0 to -pi/2 for the region counterclockwise from north to south
    // and 0 to pi/2 for the region clockwise from north to south
    // We convert this scheme to degrees that go from 0 to 360 degrees clockwise from north.

    public void setUserOrientation(Float orientation) {
        userBearing =  orientation * 180.0 / Math.PI;
        if (userBearing < 0) {
           userBearing = 360.0 - Math.abs(userBearing);
        }
        setUserBearingString();
    }

    public Double getNextStepBearing() {
        return nextStepBearing;
    }


    public String getUserDirectionString() {
        return userDirectionString;
    }

    public void setUserBearingString() {
        if (userBearing > 0 && userBearing <= 45) {
            userDirectionString = directions.get(0);
        } else if (userBearing > 45 && userBearing <= 90) {
            userDirectionString = directions.get(1);
        } else if (userBearing > 90 && userBearing <= 135) {
            userDirectionString = directions.get(2);
        } else if (userBearing > 135 && userBearing <= 180) {
            userDirectionString = directions.get(3);
        } else if (userBearing > 180 && userBearing <= 225) {
            userDirectionString = directions.get(4);
        } else if (userBearing > 225 && userBearing <= 270) {
            userDirectionString = directions.get(5);
        } else if (userBearing > 270 && userBearing <= 315) {
            userDirectionString = directions.get(6);
        } else if (userBearing > 315 && userBearing <= 360) {
            userDirectionString = directions.get(7);
        }
    }
}
