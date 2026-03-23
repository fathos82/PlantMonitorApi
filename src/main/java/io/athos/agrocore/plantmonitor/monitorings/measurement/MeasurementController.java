package io.athos.agrocore.plantmonitor.monitorings.measurement;

import io.athos.agrocore.plantmonitor.monitorings.PlantMonitoringService;
import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.CreatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.PlantMonitoringResponse;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.ChangeSensorRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.dtos.MeasurementResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/plants/{plantId}/measurements/")
public class MeasurementController {
    @Autowired
    MeasurementService measurementService;

    @PostMapping // todo: maybe add type (ex: temperature)
    public ResponseEntity<Void> addMeasurement( @PathVariable Long plantId, @Valid @RequestBody AddMeasurementRequest request){
        measurementService.createMeasurement(plantId, request);
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


    @GetMapping("{measurementType}/")
    public ResponseEntity<List<MeasurementResponse>> listMeasurementByParentAndMeasurementType(
            @PathVariable Long plantId,
            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince,
            @PathVariable MeasurementType measurementType){
//        // case has not measurementType
        if (ifModifiedSince != null && measurementService.hasMonitoringModifiedSince(ifModifiedSince)){
            ResponseEntity.status(HttpStatus.NOT_MODIFIED).lastModified(measurementService.getLastModified(plantId, measurementType)).build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .lastModified(measurementService.getLastModified(plantId, measurementType))
                .body(measurementService
                        .listMeasurementByParent(plantId, measurementType, ifModifiedSince)
                        .stream()
                        .map(MeasurementResponse::new).toList());
    }

    @GetMapping
    public ResponseEntity<List<MeasurementResponse>> listMeasurementByParent(
            @PathVariable Long plantId,
            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(measurementService
                        .listMeasurementByParent(plantId, ifModifiedSince)
                        .stream()
                        .map(MeasurementResponse::new).toList());
    }



}
