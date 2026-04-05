package io.athos.agrocore.plantmonitor.devices.sensors;
import io.athos.agrocore.plantmonitor.devices.sensors.dtos.*;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors/")
public class SensorController {
    @Autowired
    private SensorService sensorService;

    @PostMapping
    public ResponseEntity<SensorResponse> registerSensor(@Valid @RequestBody RegisterSensorRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(new SensorResponse(sensorService.createSensor(request, authenticatedUser)));
    }


    @PostMapping("from_device/{sensorId}/errors/")
    public ResponseEntity<Void> registerError(@PathVariable Long sensorId, @RequestBody CreateSensorMessageError request){
        sensorService.registerError(sensorId, request.message());
        return ResponseEntity.ok().build();
    }

    // TODO: No futuro criar controller propria.
    @GetMapping("templates/")
    public ResponseEntity<List<SensorTemplate>> listTemplates(){
        return ResponseEntity.ok(sensorService.listTemplates());
    }

    @GetMapping("{sensorId}/errors/")
    public ResponseEntity<Page<SensorMessageError>> getErrors(
            @PathVariable Long sensorId,
            @AuthenticationPrincipal SecurityUser authenticatedUser,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC

            ) Pageable pageable


    ) {
        return ResponseEntity.ok(sensorService.listError(sensorId, pageable, authenticatedUser).map(SensorMessageError::new));
    }



    @PatchMapping("{sensorId}/")
    public ResponseEntity<SensorResponse> updateSensor(@PathVariable Long sensorId, @Valid @RequestBody UpdateSensorRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(new SensorResponse(sensorService.updateSensor(sensorId,request, authenticatedUser)));
    }
    @DeleteMapping("{sensorId}/")
    public ResponseEntity<SensorResponse> deleteSensor(@PathVariable Long sensorId, @AuthenticationPrincipal SecurityUser authenticatedUser){
        sensorService.deleteSensor(sensorId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    // TODO: Depois trocar essa url

    // TODO: List All By Default
    @GetMapping
    public ResponseEntity<List<SensorResponse>> listAllByDeviceId(@Valid @RequestParam Long deviceId, @AuthenticationPrincipal SecurityUser authenticatedUser){
        return ResponseEntity.ok(sensorService.listSensorByDeviceId(deviceId, authenticatedUser).stream().map(SensorResponse::new).toList());
    }

    // TODO: depois repensar nessa rota.
    @GetMapping("from_device/")
    public ResponseEntity<List<SensorResponse>> listSensorByDeviceUuid(@Valid @RequestParam String deviceUid){
        return ResponseEntity.ok(sensorService.listSensorByDeviceUuid(deviceUid).stream().map(SensorResponse::new).toList());
    }
}
