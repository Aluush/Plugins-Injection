package org.sid.calculator.request;

public  class PluginRequest {
    private String name;
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    // Getter et setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}