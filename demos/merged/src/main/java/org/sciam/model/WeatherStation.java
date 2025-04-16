package org.sciam.model;

import java.util.concurrent.ThreadLocalRandom;

public record WeatherStation(String id, double meanTemperature) {
    public double measurement() {
        double m = ThreadLocalRandom.current().nextGaussian(meanTemperature, 10);
        return Math.round(m * 10.0) / 10.0;
    }
}