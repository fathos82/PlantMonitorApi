package io.athos.agrocore.plantmonitor.monitorings.measurement;

import io.athos.agrocore.plantmonitor.devices.sensors.Proto;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementResponse;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/measurements/")
public class MeasurementController {
    @Autowired
    MeasurementService measurementService;

    @PostMapping // todo: maybe add type (ex: temperature)
    public ResponseEntity<Void> addMeasurement(@Valid @RequestBody AddMeasurementRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        measurementService.createMeasurement(request, authenticatedUser);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("{measurementId}/")
    public ResponseEntity<MeasurementResponse> changeSensorFromMeasurement(@PathVariable Long measurementId, @Valid @RequestBody ChangeSensorRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(new MeasurementResponse(measurementService.changeSensor(measurementId, request, authenticatedUser)));
    }

    @DeleteMapping("{measurementId}") // todo: maybe add type (ex: temperature)
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long measurementId, @AuthenticationPrincipal SecurityUser authenticatedUser){
        measurementService.deleteMeasurement(measurementId,authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{measurementId}/history/view/")
    public ResponseEntity<List<MeasurementValueView>> listMeasurementByParentWithView(
            @PathVariable Long measurementId,
            @RequestParam(value = "start", required = true) Instant start,
            @RequestParam(value = "end",  required = true) Instant end,
            @RequestParam(value = "limit", defaultValue = " 2147483644") Integer limit
            ){

        return ResponseEntity.ok(measurementService.listMeasurementByParentWithView(measurementId,  start, end, limit));
    }

    @GetMapping(value = "{measurementId}/history/protobuf/", produces = "application/x-protobuf")
    public ResponseEntity<Proto.SensorReadingsResponse> listMeasurementByParentWithProtoBuffer(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "limit", defaultValue = " 2147483644") Integer limit
    ){

        return ResponseEntity.ok(measurementService.listMeasurementByParentWithProtoBuffer(measurementId,  start, end, limit));
    }

    @GetMapping(value = "{measurementId}/history/protobuf_parallel/", produces = "application/x-protobuf")
    public ResponseEntity<Proto.SensorReadingsResponse> listMeasurementByParentWithProtoBufferParallel(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "limit", defaultValue = " 2147483644") Integer limit
    ){

        return ResponseEntity.ok(measurementService.listMeasurementByParentWithProtoBufferParallel(measurementId,  start, end, limit));
    }



    @GetMapping("{measurementId}/history/")
    public ResponseEntity<List<Object[]>> listMeasurementByParent(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "limit", defaultValue = "99999999") Integer limit
    ){
        return ResponseEntity.ok(measurementService.listMeasurementByParent(measurementId,  start, end, limit));
    }



}
