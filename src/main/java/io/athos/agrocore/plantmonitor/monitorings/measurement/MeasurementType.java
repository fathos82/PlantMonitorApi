package io.athos.agrocore.plantmonitor.monitorings.measurement;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.athos.agrocore.plantmonitor.NormalizedEnum;

public enum MeasurementType {
    TEMPERATURE,
    DISTANCE,
    HUMIDITY,
    AIR_QUALITY,
    SOIL_MOISTURE,

    IRRIGATION_STATUS,

    PLANT_HEALTH;

    @JsonCreator
    public static MeasurementType fromString(String value) {
        return NormalizedEnum.fromJson(MeasurementType.class, value);
    }

    }