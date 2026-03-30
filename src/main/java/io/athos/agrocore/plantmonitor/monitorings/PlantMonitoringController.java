package io.athos.agrocore.plantmonitor.monitorings;

import io.athos.agrocore.plantmonitor.monitorings.dtos.CreatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.PlantMonitoringResponse;
import io.athos.agrocore.plantmonitor.monitorings.dtos.UpdatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/plants/") // todo: change this route
public class  PlantMonitoringController {
    @Autowired
    PlantMonitoringService plantMonitoringService;
    @PostMapping
    public ResponseEntity<PlantMonitoringResponse> createPlantMonitoring(@Valid @RequestBody CreatePlantMonitoringRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(new PlantMonitoringResponse(plantMonitoringService.createPlantMonitoring(request, authenticatedUser)));
    }

    @PatchMapping("{plantMonitoringId}/")
    public ResponseEntity<PlantMonitoringResponse> updatePlantMonitoring(@PathVariable Long plantMonitoringId,  @Valid @RequestBody UpdatePlantMonitoringRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(new PlantMonitoringResponse(plantMonitoringService.updatePlantMonitoringId(plantMonitoringId, request, authenticatedUser)));
    }


    @DeleteMapping("{plantMonitoringId}/")
    public ResponseEntity<Void> deletePlantMonitoring(@PathVariable Long plantMonitoringId, @AuthenticationPrincipal SecurityUser authenticatedUser){
        plantMonitoringService.deletePlantMonitoring(plantMonitoringId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{plantMonitoringId}/")
    public ResponseEntity<PlantMonitoringResponse> findPlantMonitoringById(@PathVariable Long plantMonitoringId, @AuthenticationPrincipal SecurityUser authenticatedUser) {
        return ResponseEntity.ok(new PlantMonitoringResponse(plantMonitoringService.findById(plantMonitoringId, authenticatedUser)));
    }

    @GetMapping("{plantMonitoringId}/")
    public ResponseEntity<List<PlantMonitoringResponse>> findAllPlantMonitoring(@AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(plantMonitoringService.findAll(authenticatedUser)
                .stream().map(PlantMonitoringResponse::new)
                .toList()
        );
    }

//    @PostMapping("{plantId}/measurement/") // todo: maybe add type (ex: temperature)
//    public void addMeasurement(@ AddMeasurementRequest request ){
//
//    }
}
