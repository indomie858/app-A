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
        // This comes from the mapbox camera,
        // which will be given in degrees from 0 to 360.
        // 0 = north, 90 = west, etc.

        // Transform to radians.
        nextStepBearing = nextStepBearing * Math.PI / 180.0;
        this.nextStepBearing = nextStepBearing;
    }

    public void setUserOrientation(Float orientation) {
        // Ensure this is in the range 0 to 2π.
        Double bearing = orientation.doubleValue();
        if (bearing < 0) {
            bearing = Math.PI * 2 + bearing;
        }
        userBearing = bearing.doubleValue();
    }

    public Double directionDiff(Double bearing, Double target) {
        // Stupid way of getting the radian angle of the
        // bearing from the target in the range [0, 2pi]
        // With the target set to 0.
        Double retBearing = Math.asin(Math.sin(bearing - target));
        return retBearing;
    }

    public String getBearingInstruction() {
        String instruction = "";
        Double directionDiff = directionDiff(userBearing, nextStepBearing);
        if (directionDiff >= -0.3926991 && directionDiff <= 0.3926991) {
             instruction = "Go straight";
        } else if (directionDiff >= -1.265364 && directionDiff < -0.3926991) {
            instruction = "Turn slight clockwise.";
        } else if (directionDiff > 0.3926991 && directionDiff <= 1.265364) {
            instruction = "Turn slight counterclockwise.";
        } else if (directionDiff > 1.265364 && directionDiff <= 1.9634954) {
            instruction = "Face left.";
        } else if (directionDiff >= -1.9634954 && directionDiff < -1.265364) {
            instruction = "Face right.";
        } else {
            instruction = "Turn around.";
        }
        return instruction;
    }

/*
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
*/
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
