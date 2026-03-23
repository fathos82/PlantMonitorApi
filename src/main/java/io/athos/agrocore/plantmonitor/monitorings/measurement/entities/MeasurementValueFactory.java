package io.athos.agrocore.plantmonitor.monitorings.measurement.entities;

import io.athos.agrocore.plantmonitor.errors.InvalidSensorType;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementValueAndDtoPair;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ScalarMeasurementValueDTO;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class MeasurementValueFactory {
    @SuppressWarnings("unchecked")
    public MeasurementValueAndDtoPair createMeasurement(MeasurementType measurementType, JsonNode root, ObjectMapper objectMapper) {
        switch (measurementType) {


            case TEMPERATURE, DISTANCE,  HUMIDITY, AIR_QUALITY -> {
                ScalarMeasurementValueDTO dto =
                        objectMapper.treeToValue(root, ScalarMeasurementValueDTO.class);
                ScalarMeasurementValue scalarMeasurement = new ScalarMeasurementValue();
                scalarMeasurement.setValue(dto.value());
                return new MeasurementValueAndDtoPair(scalarMeasurement, dto) ;
            }
            default -> throw new InvalidSensorType();
        }
    }




}
