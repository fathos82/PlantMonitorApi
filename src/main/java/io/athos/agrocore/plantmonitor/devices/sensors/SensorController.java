package io.athos.agrocore.plantmonitor.devices.sensors;

import io.athos.agrocore.plantmonitor.devices.dtos.DeviceResponse;
import io.athos.agrocore.plantmonitor.devices.sensors.dtos.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors/")
public class SensorController {
    @Autowired
    private SensorService sensorService;

    @PostMapping
    public ResponseEntity<SensorResponse> registerSensor(@Valid @RequestBody RegisterSensorRequest request){
        return ResponseEntity.ok(new SensorResponse(sensorService.createSensor(request)));
    }


    @PostMapping("{sensorId}/errors/")
    public ResponseEntity<Void> registerError(@PathVariable Long sensorId, @RequestBody CreateSensorMessageError request){
        sensorService.registerError(sensorId, request.message());
        return ResponseEntity.ok().build();
    }

    @GetMapping("{sensorId}/errors/")
    public ResponseEntity<Page<SensorMessageError>> getErrors(
            @PathVariable Long sensorId,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(sensorService.listError(sensorId, pageable).map(SensorMessageError::new));
    }



    @PatchMapping("{sensorId}/")
    public ResponseEntity<SensorResponse> updateSensor(@PathVariable Long sensorId, @Valid @RequestBody UpdateSensorRequest request){
        return ResponseEntity.ok(new SensorResponse(sensorService.updateSensor(sensorId,request)));
    }
    @DeleteMapping("{sensorId}/")
    public ResponseEntity<SensorResponse> deleteSensor(@PathVariable Long sensorId){
        sensorService.deleteSensor(sensorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SensorResponse>> listSensorByDiveUuid(@Valid @RequestParam String deviceUid){
        return ResponseEntity.ok(sensorService.listSensorByDiveUuid(deviceUid).stream().map(SensorResponse::new).toList());
    }
}
