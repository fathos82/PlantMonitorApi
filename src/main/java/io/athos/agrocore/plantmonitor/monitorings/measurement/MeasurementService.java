package io.athos.agrocore.plantmonitor.monitorings.measurement;
import io.athos.agrocore.plantmonitor.devices.sensors.Proto;
import io.athos.agrocore.plantmonitor.devices.sensors.SensorService;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoringService;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeasurementService {
    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired

    private PlantMonitoringService plantMonitoringService;
    @Autowired
    private MeasurementValueProtoRepository measurementValueProtoRepository;

    @Autowired
    private TimeBucketCalculator bucketCalculator;
    @Autowired
    private SensorService sensorService;
    @Autowired
    MeasurementValueRepository measurementValueRepository;


// TODO:   server.compression.enabled=true

    public Measurement createMeasurement(AddMeasurementRequest request, SecurityUser authenticatedUser){
        Measurement  measurement = new Measurement();

        /// todo: verificar se o sensor de capacidade de monitorar essa medida
        measurement.setMeasurementType(request.measurementType());
        measurement.setPlantMonitoring(plantMonitoringService.findById(request.plantId(), authenticatedUser));
        measurement.setVirtualSensor(sensorService.getSensorByIdAndAuthenticatedUser(request.sensorId(), authenticatedUser));
        measurementRepository.save(measurement);
        return measurement;
    }

    @Transactional
    public void saveFromBatch(MeasurementType capability, Long sensorId, Proto.SensorReadingBatch batch) {
        System.out.println("OI");
        List<MeasurementValue> measurementValues = new ArrayList<>();
        Measurement measurement = measurementRepository.findByVirtualSensorIdAndMeasurementType(sensorId, capability).orElseThrow(() -> new NotFoundException("measurement", "capability", capability.toString()));
        System.out.println(measurement);
        measurement.getVirtualSensor().onDataReceived();
        long baseTimestamp = batch.getBaseTimestamp();
        System.out.println(batch.toString());
        for (Proto.SensorReading sensorReading : batch.getReadingsList()) {
            long timestampRealMs = baseTimestamp + sensorReading.getDeltaMs();
            MeasurementValue measurementValue = new MeasurementValue(measurement,Instant.ofEpochMilli(timestampRealMs), Math.round(sensorReading.getValue() * 100.0) / 100.0);
            measurementValues.add(measurementValue);
        }
        System.out.println(measurementValues.toString());
        measurementValueRepository.saveAll(measurementValues);
    }



    Measurement findById(Long measurementId) {
        return measurementRepository.findById(measurementId).orElseThrow(() -> new NotFoundException(Measurement.class.getSimpleName(), measurementId));
    }

    Measurement getByIdAndAuthenticatedUser(Long measurementId, SecurityUser authenticatedUser) {
        return measurementRepository.findByIdAndVirtualSensor_Device_User_Id(measurementId, authenticatedUser.getPersistentUser().getId()).orElseThrow(() -> new NotFoundException(Measurement.class.getSimpleName(), measurementId));
    }
    public Measurement changeSensor(Long measurementId, ChangeSensorRequest request, SecurityUser authenticatedUser) {
        Measurement measurement =  findById(measurementId);
        VirtualSensor virtualSensor = sensorService.getSensorByIdAndAuthenticatedUser(request.sensorId(), authenticatedUser);
        measurement.setVirtualSensor(virtualSensor);
        return measurementRepository.save(measurement);
    }

    @Transactional
    public void deleteMeasurement(Long measurementId, SecurityUser authenticatedUser) {
        Measurement measurement = getByIdAndAuthenticatedUser(measurementId, authenticatedUser);
        measurementValueRepository.deleteByMeasurementParentId(measurementId);
        measurementRepository.delete(measurement);
    }

    public  List<Measurement> listAllMeasurementFromUser(SecurityUser authenticatedUser) {
        return measurementRepository.findAllByPlantMonitoring_User_Id(authenticatedUser.getPersistentUser().getId());
    }

    public MeasurementValueResponse listMeasurementByParentId(Long measurementId, Instant start, Instant end, Integer targetPoints) {
        MeasurementStats measurementStats = measurementValueRepository.findStats(measurementId, start, end);
        var values =  measurementValueRepository.findByMeasurementParentIdDownsampling(measurementId, start, end, targetPoints);
        return new MeasurementValueResponse(measurementStats.getMin(), measurementStats.getMax(), measurementStats.getAvg(), values);
    }




    public Proto.SensorReadingsResponse listMeasurementByParentWithProtoBuffer(Long measurementId, Instant start, Instant end, Integer limit) {
        long startTime = System.nanoTime();
        System.out.printf("[SERVICE][PROTO] measurementId=%d, start=%s, end=%s, limit=%d%n", measurementId, start, end, limit);
        Proto.SensorReadingsResponse response = measurementValueProtoRepository.findAsProto(measurementId, start, end, limit);
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.printf("[SERVICE][PROTO] Number of readings=%d, Duration=%dms%n",
                response.getReadingsCount(), durationMs);

        return response;

    }



    public void detachSensorFromMeasurements(Long sensorId, SecurityUser authenticatedUser) {
        measurementRepository.detachSensorFromMeasurements(sensorId, authenticatedUser.getPersistentUser().getId());
    }
}
