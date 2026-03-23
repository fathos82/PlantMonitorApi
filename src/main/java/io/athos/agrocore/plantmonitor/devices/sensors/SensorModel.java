package io.athos.agrocore.plantmonitor.devices.sensors;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.athos.agrocore.plantmonitor.NormalizedEnum;

public enum SensorModel {
    HC_SR04;

    @JsonCreator
    public static SensorModel fromValue(String value) {
        return NormalizedEnum.fromJson(SensorModel.class, value);
    }
}
