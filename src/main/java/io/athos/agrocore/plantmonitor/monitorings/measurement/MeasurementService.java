package io.athos.agrocore.plantmonitor.monitorings.measurement;
import io.athos.agrocore.plantmonitor.devices.sensors.Proto;
import io.athos.agrocore.plantmonitor.devices.sensors.SensorService;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoringService;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementResponse;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
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
    private ObjectMapper objectMapper;
    @Autowired
    private SensorService sensorService;
    @Autowired
    ValueMeasurementRepository measurementValueRepository;


// TODO:   server.compression.enabled=true

    public Measurement createMeasurement(AddMeasurementRequest request, SecurityUser authenticatedUser){
        Measurement  measurement = new Measurement();

        /// todo: verificar se o sensor de capacidade de monitorar essa medida
        measurement.setMeasurementType(request.measurementType());
        measurement.setPlantMonitoring(plantMonitoringService.findPlantMonitoringById(request.plantId(), authenticatedUser));
        measurement.setVirtualSensor(sensorService.getSensorByIdAndAuthenticatedUser(request.sensorId(), authenticatedUser));
        measurementRepository.save(measurement);
        return measurement;
    }


    public void saveAll(MeasurementType capability,Long sensorId, Proto.SensorReadingBatch batch) {
        List<MeasurementValue> measurementValues = new ArrayList<>();
        Measurement measurement = measurementRepository.findByVirtualSensorIdAndMeasurementType(sensorId, capability).orElseThrow(() -> new NotFoundException("measurement", "capability", capability.toString()));
        long baseTimestamp = batch.getBaseTimestamp();
        for (Proto.SensorReading sensorReading : batch.getReadingsList()) {
            long timestampRealMs = baseTimestamp + sensorReading.getDeltaMs();
            MeasurementValue measurementValue = new MeasurementValue(measurement,Instant.ofEpochMilli(timestampRealMs), Math.round(sensorReading.getValue() * 100.0) / 100.0);
            measurementValues.add(measurementValue);
        }
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

    public void deleteMeasurement(Long measurementId, SecurityUser authenticatedUser) {
        Measurement measurement = getByIdAndAuthenticatedUser(measurementId, authenticatedUser);
        measurementRepository.delete(measurement);
    }

    public  List<Measurement> listAllMeasurementFromUser(SecurityUser authenticatedUser) {
        return measurementRepository.findAllByPlantMonitoring_User_Id(authenticatedUser.getPersistentUser().getId());
    }

    public List<MeasurementValueView> listMeasurementByParentWithView(Long measurementId, Instant start, Instant end, int limit) {
        long startTime = System.nanoTime();
        System.out.printf("[SERVICE][VIEW] measurementId=%d, start=%s, end=%s, limit=%d%n",
                measurementId, start, end, limit);

        List<MeasurementValueView> values = measurementValueRepository.findMeasurementValuesWithView(measurementId, start, end, limit);

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.printf("[SERVICE][VIEW] Result size=%d, Duration=%dms%n", values.size(), durationMs);

        return values;
    }

    public Proto.SensorReadingsResponse listMeasurementByParentWithProtoBuffer(Long measurementId, Instant start, Instant end, Integer limit) {

        long startTime = System.nanoTime();
        System.out.printf("[SERVICE][PROTO] measurementId=%d, start=%s, end=%s, limit=%d%n",
                measurementId, start, end, limit);

        List<MeasurementValueView> values = listMeasurementByParentWithView(measurementId, start, end, limit);

        Proto.SensorReadingsResponse.Builder readingsBuilder = Proto.SensorReadingsResponse.newBuilder();
        for (MeasurementValueView mv : values) {
            readingsBuilder.addReadings(
                    Proto.SensorReadingResponse.newBuilder()
                            .setTimestamp(mv.getTimestamp().toEpochMilli())
                            .setValue((float) mv.getValue())
                            .build()
            );
        }

        Proto.SensorReadingsResponse response = readingsBuilder.build();
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.printf("[SERVICE][PROTO] Number of readings=%d, Duration=%dms%n",
                response.getReadingsCount(), durationMs);

        return response;
    }

    public Proto.SensorReadingsResponse listMeasurementByParentWithProtoBufferParallel(
            Long measurementId, Instant start, Instant end, Integer limit) {

        long startTime = System.nanoTime();
        System.out.printf("[SERVICE][PROTO_PARALLEL] measurementId=%d, start=%s, end=%s, limit=%d%n",
                measurementId, start, end, limit);

        List<MeasurementValueView> values = listMeasurementByParentWithView(measurementId, start, end, limit);

        List<Proto.SensorReadingResponse> readingList = values.parallelStream()
                .map(mv -> Proto.SensorReadingResponse.newBuilder()
                        .setTimestamp(mv.getTimestamp().toEpochMilli())
                        .setValue((float) mv.getValue())
                        .build())
                .toList();

        Proto.SensorReadingsResponse.Builder readingsBuilder = Proto.SensorReadingsResponse.newBuilder();
        readingsBuilder.addAllReadings(readingList);

        Proto.SensorReadingsResponse response = readingsBuilder.build();
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.printf("[SERVICE][PROTO_PARALLEL] Number of readings=%d, Duration=%dms%n",
                response.getReadingsCount(), durationMs);

        return response;
    }

    public List<Object[]> listMeasurementByParent(
            Long measurementId, Instant start, Instant end, Integer limit) {
        return measurementValueRepository.findRaw(measurementId, start, end, limit);
    }


}
