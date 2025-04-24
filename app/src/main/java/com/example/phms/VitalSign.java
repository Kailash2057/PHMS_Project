package com.example.phms;

public class VitalSign {
    private double bloodPressure;
    private double glucoseLevel;
    private double cholesterol;

    public VitalSign(double bp, double glucose, double cholesterol){
        this.bloodPressure = bp;
        this.glucoseLevel = glucose;
        this.cholesterol = cholesterol;
    }

    public double getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(double bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public double getGlucoseLevel() {
        return glucoseLevel;
    }

    public void setGlucoseLevel(double glucoseLevel) {
        this.glucoseLevel = glucoseLevel;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }
}
