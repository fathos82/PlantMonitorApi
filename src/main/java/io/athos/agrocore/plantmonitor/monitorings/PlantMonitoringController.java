package io.athos.agrocore.plantmonitor.monitorings;

import io.athos.agrocore.plantmonitor.monitorings.dtos.AddMeasurementRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.CreatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.PlantMonitoringResponse;
import io.athos.agrocore.plantmonitor.monitorings.dtos.UpdatePlantMonitoringRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/plants/") // todo: change this route
public class  PlantMonitoringController {
    @Autowired
    PlantMonitoringService plantMonitoringService;
    @PostMapping
    public ResponseEntity<PlantMonitoringResponse> createPlantMonitoring(@Valid @RequestBody CreatePlantMonitoringRequest request){
        return ResponseEntity.ok(new PlantMonitoringResponse(plantMonitoringService.createPlantMonitoring(request)));
    }

    @PatchMapping("{plantMonitoringId}/")
    public ResponseEntity<PlantMonitoringResponse> updatePlantMonitoring(@PathVariable Long plantMonitoringId,  @Valid @RequestBody UpdatePlantMonitoringRequest request){
        return ResponseEntity.ok(new PlantMonitoringResponse(plantMonitoringService.updatePlantMonitoringId(plantMonitoringId, request)));
    }


    @DeleteMapping("{plantMonitoringId}/")
    public ResponseEntity<Void> deletePlantMonitoring(@PathVariable Long plantMonitoringId){
        plantMonitoringService.deletePlantMonitoring(plantMonitoringId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{plantMonitoringId}/")
    public ResponseEntity<PlantMonitoringResponse> findPlantMonitoringById(@PathVariable Long plantMonitoringId){
        return ResponseEntity.ok(new PlantMonitoringResponse(plantMonitoringService.findPlantMonitoringById(plantMonitoringId)));
    }

//    @PostMapping("{plantId}/measurement/") // todo: maybe add type (ex: temperature)
//    public void addMeasurement(@ AddMeasurementRequest request ){
//
//    }
}
