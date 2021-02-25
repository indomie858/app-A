package com.example.appa.viewmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompassViewModel {
    private Double nextStepBearing;
    private Double userBearing;

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
        // This function returns the clockwise angle
        // from the target to the user's bearing.
        Double retBearing;
        if (bearing > target) {
            // If bearing leads target,
            // bearing - target gives
            // clockwise angle from target to bearing.
            retBearing = bearing - target;
        } else {
            // if target leads bearing on the unit circle,
            // target - bearing gives counterclockwise angle
            // 2pi - angle gives the clockwise angle from target to bearing.
            retBearing = Math.PI * 2 - (target - bearing);
        }
        return retBearing;
    }
    public boolean isOriented() {
        Double directionDiff = directionDiff(userBearing, nextStepBearing);
        return directionDiff >= 5.8904862 || directionDiff <= 0.3926991;
    }

    public String getBearingInstruction() {
        String instruction = "";
        Double directionDiff = directionDiff(userBearing, nextStepBearing);
        if (directionDiff >= 5.8904862 || directionDiff <= 0.3926991) {
             instruction = "You are facing " + getUserCardinalDirection() + ". ";
        } else if (directionDiff >= 5.1050881 && directionDiff < 5.8904862) {
            instruction = "Turn slight clockwise.";
        } else if (directionDiff > 0.3926991 && directionDiff <= 1.265364) {
            instruction = "Turn slight counterclockwise.";
        } else if (directionDiff > 1.265364 && directionDiff <= 1.9634954) {
            instruction = "Face left.";
        } else if (directionDiff >= 4.3196899 && directionDiff < 5.8904862) {
            instruction = "Face right.";
        } else {
            instruction = "Turn around.";
        }
        return instruction;
    }

    public String getUserCardinalDirection() {
        String direction = "";
        if (userBearing > 5.8904862 || userBearing <= 0.3926991) {
            direction = "North";
        } else if (userBearing >= 5.1050881 && userBearing < 5.8904862) {
            direction = "Northeast";
        } else if (userBearing > 0.3926991 && userBearing <= 1.265364) {
            direction = "Northwest";
        } else if (userBearing > 1.265364 && userBearing <= 1.9634954) {
            direction = "West";
        } else if (userBearing > 4.3196899 && userBearing <= 5.8904862) {
            direction = "East";
        } else if (userBearing > 1.9634954 && userBearing <= 2.7488936) {
            direction = "Southwest";
        } else if (userBearing >= 2.7488936 && userBearing <= 3.5342917) {
            direction = "South";
        } else if (userBearing > 3.5342917 && userBearing <= 4.3196899) {
            direction = "Southeast";
        }
        return direction;
    }

}
