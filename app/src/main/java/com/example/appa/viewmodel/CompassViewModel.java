package com.example.appa.viewmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompassViewModel {
    private Double nextStepBearing;
    private Double userBearing;

    List<String> directions = Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "NW");
    List<Integer> directionCodes = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
    Integer directionCode;

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
    }

    public Double getNextStepBearing() {
        return nextStepBearing;
    }

    public String getBearingInstruction() {
        int nextStepCode = getDirectionCode(nextStepBearing);
        int userBearingCode = getDirectionCode(userBearing);
        int diff = 4 - nextStepCode;
        if (nextStepCode == userBearingCode) {
            return "Go straight.";
        }  else if ((userBearingCode + diff) % 8 > ((nextStepCode + diff) % 8)) {
            return "Turn counterclockwise.";
        } else {
            return "Turn clockwise.";
        }
    }

    public String getNextStepDirectionString() {
        return directions.get(getDirectionCode(nextStepBearing));
    }
    public String getUserDirectionString() {
        return directions.get(getDirectionCode(userBearing));
    }

    public Integer getDirectionCode(Double bearing) {
        Integer directionCode = 0;
        if (bearing >= 0 && bearing <= 45) {
            directionCode = 0;
        } else if (bearing > 45 && bearing <= 90) {
            directionCode = 1;
        } else if (bearing > 90 && bearing <= 135) {
            directionCode = 2;
        } else if (bearing > 135 && bearing <= 180) {
            directionCode = 3;
        } else if (bearing > 180 && bearing <= 225) {
            directionCode = 4;
        } else if (bearing > 225 && bearing <= 270) {
            directionCode = 5;
        } else if (bearing > 270 && bearing <= 315) {
            directionCode = 6;
        } else if (bearing > 315 && bearing <= 360) {
            directionCode = 7;
        }
        return directionCode;
    }

}
