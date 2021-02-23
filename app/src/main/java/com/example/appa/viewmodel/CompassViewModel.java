package com.example.appa.viewmodel;

public class CompassViewModel {
    private Double nextStepBearing;
    private Double userBearing;

    public void setNextStepBearing(Double nextStepBearing) {
        this.nextStepBearing = nextStepBearing;
    }

    public void setUserOrientation(Float orientation) {
        userBearing = orientation.doubleValue();
    }

    public Double getNextStepBearing() {
        return nextStepBearing;
    }

    public Double getUserBearing() {
        Double bearingDegrees = userBearing * 180.0 / Math.PI;
        if (bearingDegrees < 0) {
            bearingDegrees = 360.0 - Math.abs(bearingDegrees);
        }
        return bearingDegrees;
    }

}
