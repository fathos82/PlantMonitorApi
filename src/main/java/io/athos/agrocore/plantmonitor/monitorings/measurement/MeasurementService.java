package io.athos.agrocore.plantmonitor.monitorings.measurement;
import io.athos.agrocore.plantmonitor.devices.sensors.Proto;
import io.athos.agrocore.plantmonitor.devices.sensors.SensorService;
import io.athos.agrocore.plantmonitor.devices.sensors.VirtualSensor;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoringService;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public void createMeasurement(AddMeasurementRequest request){
        Measurement  measurement = new Measurement();

        /// todo: verificar se o sensor de capacidade de monitorar essa medida
        measurement.setMeasurementType(request.measurementType());
        measurement.setPlantMonitoring(plantMonitoringService.findPlantMonitoringById(request.plantId()));
        measurement.setVirtualSensor(sensorService.getSensorById(request.sensorId()));
        measurementRepository.save(measurement);
//        return measurement;
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

    public  List<MeasurementValue> listMeasurementValue(Long plantId, MeasurementType measurementType, String ifModifiedSince) {
        if (ifModifiedSince != null) {
            return measurementValueRepository.findAllByMeasurementParent_PlantMonitoring_IdAndTimestampAfter(plantId, parseIfModifiedSince(ifModifiedSince).toInstant());
        }
        return measurementValueRepository.findAllByMeasurementParent_PlantMonitoring_IdAndMeasurementParent_MeasurementType(plantId, measurementType);
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
        return measurementValueRepository.existsByTimestampAfter(parseIfModifiedSince(ifModifiedSince).toInstant());
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
                measurementValueRepository.findLastModified(plantId, measurementType);

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
    public List<MeasurementValueView> listMeasurementByParentWithView(Long measurementId, Instant start, Instant end, int limit) {
        long startTime = System.nanoTime();
        System.out.printf("[SERVICE][VIEW] measurementId=%d, start=%s, end=%s, limit=%d%n",
                measurementId, start, end, limit);

        List<MeasurementValueView> values = measurementValueRepository.findMeasurementValuesWithView(measurementId, start, end, limit);

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.printf("[SERVICE][VIEW] Result size=%d, Duration=%dms%n", values.size(), durationMs);

        return values;
    }

    public Proto.SensorReadingsResponse listMeasurementByParentWithProtoBuffer(
            Long measurementId, Instant start, Instant end, Integer limit) {

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
}
