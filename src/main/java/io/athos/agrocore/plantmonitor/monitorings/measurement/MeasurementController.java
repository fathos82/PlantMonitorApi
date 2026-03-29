package io.athos.agrocore.plantmonitor.monitorings.measurement;

import io.athos.agrocore.plantmonitor.devices.sensors.Proto;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/measurements/")
public class MeasurementController {
    @Autowired
    MeasurementService measurementService;

    @PostMapping // todo: maybe add type (ex: temperature)
    public ResponseEntity<Void> addMeasurement(@Valid @RequestBody AddMeasurementRequest request){
        measurementService.createMeasurement(request);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("{measurementId}/")
    public ResponseEntity<MeasurementResponse> changeSensorFromMeasurement(@PathVariable Long measurementId,@Valid @RequestBody ChangeSensorRequest request){
        return ResponseEntity.ok(new MeasurementResponse(measurementService.changeSensor(measurementId, request)));
    }

    @DeleteMapping("{measurementId}") // todo: maybe add type (ex: temperature)
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long measurementId){
        measurementService.deleteMeasurement(measurementId);
        return ResponseEntity.noContent().build();
    }
//    @GetMapping
//    public ResponseEntity<List<MeasurementValue>> listMeasurement(
//            @PathVariable Long plantId,
//            @RequestParam(required = true)  MeasurementType measurementType,
//            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince){
//        // case has not measurementType
//        if (ifModifiedSince != null && measurementService.hasMonitoringModifiedSince(ifModifiedSince)){
//            ResponseEntity.status(HttpStatus.NOT_MODIFIED).lastModified(measurementService.getLastModified(plantId, measurementType)).build();
//        }
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .lastModified(measurementService.getLastModified(plantId, measurementType))
//                .body(measurementService.listMeasurementValue(plantId, measurementType, ifModifiedSince));
//    }

//
//    @GetMapping("{measurementType}/")
//    public ResponseEntity<List<MeasurementResponse>> listMeasurementByParentAndMeasurementType(
//            @PathVariable Long plantId,
//            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince,
//            @PathVariable MeasurementType measurementType){
////        // case has not measurementType
//        if (ifModifiedSince != null && measurementService.hasMonitoringModifiedSince(ifModifiedSince)){
//            ResponseEntity.status(HttpStatus.NOT_MODIFIED).lastModified(measurementService.getLastModified(plantId, measurementType)).build();
//        }
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .lastModified(measurementService.getLastModified(plantId, measurementType))
//                .body(measurementService
//                        .listMeasurementByParent(plantId, measurementType, ifModifiedSince)
//                        .stream()
//                        .map(MeasurementResponse::new).toList());
//    }

    @GetMapping("{measurementId}/history/view/")
    public ResponseEntity<List<MeasurementValueView>> listMeasurementByParentWithView(
            @PathVariable Long measurementId,
            @RequestParam(value = "start", required = true) Instant start,
            @RequestParam(value = "start",  required = true) Instant end,
            @RequestParam(value = "limit", defaultValue = " 2147483644") Integer limit
            ){

        return ResponseEntity.ok(measurementService.listMeasurementByParentWithView(measurementId,  start, end, limit));
    }

    @GetMapping(value = "{measurementId}/history/protobuf/", produces = "application/x-protobuf")
    public ResponseEntity<Proto.SensorReadingsResponse> listMeasurementByParentWithProtoBuffer(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("start") Instant end,
            @RequestParam(value = "limit", defaultValue = " 2147483644") Integer limit
    ){

        return ResponseEntity.ok(measurementService.listMeasurementByParentWithProtoBuffer(measurementId,  start, end, limit));
    }

    @GetMapping(value = "{measurementId}/history/protobuf_parallel/", produces = "application/x-protobuf")
    public ResponseEntity<Proto.SensorReadingsResponse> listMeasurementByParentWithProtoBufferParallel(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("start") Instant end,
            @RequestParam(value = "limit", defaultValue = " 2147483644") Integer limit
    ){

        return ResponseEntity.ok(measurementService.listMeasurementByParentWithProtoBufferParallel(measurementId,  start, end, limit));
    }



    @GetMapping("{measurementId}/history/")
    public ResponseEntity<List<Object[]>> listMeasurementByParent(
            @PathVariable Long measurementId,
            @RequestParam("start") Instant start,
            @RequestParam("start") Instant end,
            @RequestParam(value = "limit", defaultValue = "99999999") Integer limit
    ){
        return ResponseEntity.ok(measurementService.listMeasurementByParent(measurementId,  start, end, limit));
    }



}
