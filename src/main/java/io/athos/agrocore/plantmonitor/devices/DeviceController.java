package io.athos.agrocore.plantmonitor.devices;

import io.athos.agrocore.plantmonitor.devices.dtos.CreateDeviceRequest;
import io.athos.agrocore.plantmonitor.devices.dtos.DeviceResponse;
import io.athos.agrocore.plantmonitor.devices.dtos.SetUserToDeviceRequest;
import io.athos.agrocore.plantmonitor.devices.dtos.UpdateDeviceRequest;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/devices/")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceResponse> registerDevice(@Valid @RequestBody CreateDeviceRequest request){
        Device  device = deviceService.createDevice(request);
        return ResponseEntity.ok(new DeviceResponse(device));
    }

    // TODO: REMOVE THIS
    @PostMapping("/me/")
    public ResponseEntity<DeviceResponse> setUserToDevice(@Valid @RequestBody SetUserToDeviceRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        Device  device = deviceService.setUserToDevice(authenticatedUser, request);
        return ResponseEntity.ok(new DeviceResponse(device));
    }

    @PatchMapping("{deviceId}")
    public ResponseEntity<DeviceResponse> updateDevice(@PathVariable Long deviceId, @Valid @RequestBody UpdateDeviceRequest request, @AuthenticationPrincipal SecurityUser authenticatedUser){
        Device  device = deviceService.updateDevice(deviceId, request, authenticatedUser);
        return ResponseEntity.ok(new DeviceResponse(device));
    }

    @DeleteMapping("{deviceId}/")
    public ResponseEntity<Void> deleteDeviceFromAuthenticatedUser(@PathVariable Long deviceId, @AuthenticationPrincipal SecurityUser authenticatedUser) {
        deviceService.deleteDeviceByIdFromAuthenticatedUser(deviceId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("{id}/")
    public ResponseEntity<DeviceResponse> findDeviceById(@PathVariable Long id,  @AuthenticationPrincipal SecurityUser authenticatedUser){
        Device  device = deviceService.getDeviceByIdFromAuthenticatedUser(id, authenticatedUser);
        return ResponseEntity.ok(new DeviceResponse(device));
    }

    @GetMapping("{userId}/")
    public ResponseEntity<List<DeviceResponse>> findDevicesFromAuthenticatedUser(@AuthenticationPrincipal SecurityUser authenticatedUser) {
        return ResponseEntity.ok(deviceService.findDevicesByAuthenticatedUser(authenticatedUser)
                .stream().map(DeviceResponse::new)
                .toList());
    }



}
