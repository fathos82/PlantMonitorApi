package io.athos.agrocore.plantmonitor.monitorings.measurement;
import io.athos.agrocore.plantmonitor.devices.sensors.SensorService;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoringService;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementValueDTO;
import io.athos.agrocore.plantmonitor.monitorings.measurement.entities.MeasurementValue;
import io.athos.agrocore.plantmonitor.monitorings.measurement.entities.MeasurementValueFactory;
import io.athos.agrocore.plantmonitor.monitorings.measurement.repositories.ValueMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MeasurementService {
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private PlantMonitoringService plantMonitoringService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SensorService sensorService;
    @Autowired
    ValueMeasurementRepository valueMeasurementRepository;


    @Autowired
    private MeasurementValueFactory measurementValueFactory;


    public void createMeasurement(Long measurementId, AddMeasurementRequest request){
        Measurement  measurement = new Measurement();

        /// todo: verificar se o sensor de capacidade de monitorar essa medida
        measurement.setMeasurementType(request.measurementType());
        measurement.setPlantMonitoring(plantMonitoringService.findPlantMonitoringById(measurementId));
        measurement.setVirtualSensor(sensorService.getSensorById(request.sensorId()));
        measurementRepository.save(measurement);
//        return measurement;
    }

    /*
    {
    type:value,
    type:value,
    instant,
    }
    */

    public void addValueToMeasurement (JsonNode root){
    try {
        MeasurementType measurementType = MeasurementType.fromString(root.get("type").asString());
        MeasurementValueAndDtoPair measurementPair = measurementValueFactory.createMeasurement(measurementType, root, objectMapper);
        MeasurementValueDTO measurementDTO = measurementPair.measurementDTO;
        Long sensorId = measurementDTO.sensorId();
        Optional<Measurement> measurementOptional = measurementRepository.findByVirtualSensorIdAndMeasurementType(sensorId, measurementType);
        if (measurementOptional.isPresent()) {
            Measurement measurement = measurementOptional.get();
            MeasurementValue valueMeasurement = measurementPair.measurementValue;
            valueMeasurement.setTimestamp(measurementDTO.measuredAt());
            valueMeasurement = measurement.addMeasurementValue(valueMeasurement);
            valueMeasurementRepository.save(valueMeasurement);
        }
        sensorService.activateSensor(sensorId);

    }
    catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
    }

    public  List<MeasurementValue> listMeasurementValue(Long plantId, MeasurementType measurementType, String ifModifiedSince) {
        if (ifModifiedSince != null) {
            return valueMeasurementRepository.findAllByMeasurementParent_PlantMonitoring_IdAndTimestampAfter(plantId, parseIfModifiedSince(ifModifiedSince).toInstant());
        }
        return valueMeasurementRepository.findAllByMeasurementParent_PlantMonitoring_IdAndMeasurementParent_MeasurementType(plantId, measurementType);
    }

    public  List<Measurement> listMeasurementByParent(Long plantId,MeasurementType measurementType, String ifModifiedSince) {
        if (ifModifiedSince != null) {

            if (measurementType != null) {
                return measurementRepository.findByPlant_IdAndMeasurementTypeAndValuesAfter(plantId,measurementType, parseIfModifiedSince(ifModifiedSince).toInstant());
            }
            return measurementRepository.findByPlant_IdAndValuesAfter(plantId, parseIfModifiedSince(ifModifiedSince).toInstant());
        }

        if (measurementType != null) {
            return measurementRepository.findByPlantMonitoring_IdAndMeasurementTypeOrder_ValueByTimestampAsc(plantId, measurementType);
        }
        return measurementRepository.findByPlantMonitoring_IdOrder_ValueByTimestampAsc(plantId);
    }

    public List<Measurement>  listMeasurementByParent(Long plantId, String ifModifiedSince) {
        if (ifModifiedSince != null) {
            return measurementRepository.findByPlant_IdAndValuesAfter(plantId, parseIfModifiedSince(ifModifiedSince).toInstant());
        }
        return measurementRepository.findByPlantMonitoring_IdOrder_ValueByTimestampAsc(plantId);
    }


    boolean hasMonitoringModifiedSince(String ifModifiedSince) {
        return valueMeasurementRepository.existsByTimestampAfter(parseIfModifiedSince(ifModifiedSince).toInstant());
    }

    ZonedDateTime parseIfModifiedSince(String ifModifiedSince) {
        if (ifModifiedSince == null) {
            return null;
        }

        return ZonedDateTime
                .parse(ifModifiedSince, DateTimeFormatter.RFC_1123_DATE_TIME)
                .withZoneSameInstant(ZoneOffset.UTC);
    }

    public long getLastModified(Long plantId, MeasurementType measurementType) {
        Instant lastModified =
                valueMeasurementRepository.findLastModified(plantId, measurementType);

        if (lastModified == null) {
            return -1; // padrão do Spring quando não há modificação
        }

        return lastModified.toEpochMilli();
    }

    Measurement findById(Long measurementId) {
        return measurementRepository.findById(measurementId).orElseThrow(() -> new NotFoundException(Measurement.class.getSimpleName(), measurementId));
    }
    public Measurement changeSensor(Long measurementId, ChangeSensorRequest request) {
        Measurement measurement =  findById(measurementId);
        VirtualSensor virtualSensor = sensorService.getSensorById(request.sensorId());
        measurement.setVirtualSensor(virtualSensor);
        return measurementRepository.save(measurement);
    }

    public void deleteMeasurement(Long measurementId) {
        measurementRepository.deleteById(measurementId);
    }
}
