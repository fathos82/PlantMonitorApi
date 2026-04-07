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
    public ResponseEntity<MeasurementResponse> addMeasurement(@Valid @RequestBody AddMeasurementRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){

        return ResponseEntity.ok(new MeasurementResponse(measurementService.createMeasurement(request, authenticatedUser)));
    }


    @PatchMapping("{measurementId}/")
    public ResponseEntity<MeasurementResponse> changeSensorFromMeasurement(@PathVariable Long measurementId, @Valid @RequestBody ChangeSensorRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(new MeasurementResponse(measurementService.changeSensor(measurementId, request, authenticatedUser)));
    }

    @DeleteMapping("{measurementId}/") // todo: maybe add type (ex: temperature)
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long measurementId, @AuthenticationPrincipal SecurityUser authenticatedUser){
        measurementService.deleteMeasurement(measurementId,authenticatedUser);
        return ResponseEntity.noContent().build();
    }


    @GetMapping()
    public ResponseEntity<List<MeasurementResponse>> listAllMeasurement(@AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(measurementService.listAllMeasurementFromUser(authenticatedUser)
                .stream()
                .map(MeasurementResponse::new)
                .toList());
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





    @GetMapping("{measurementId}/history/")
    public ResponseEntity<MeasurementValueResponse> listMeasurementByParent(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "targetPoints", defaultValue = "800") Integer targetPoints
    ){
        return ResponseEntity.ok(measurementService.listMeasurementByParentId(measurementId,  start, end, targetPoints));
    }



}
