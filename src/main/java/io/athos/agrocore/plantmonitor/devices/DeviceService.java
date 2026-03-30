package io.athos.agrocore.plantmonitor.devices;


import io.athos.agrocore.plantmonitor.devices.dtos.UpdateDeviceRequest;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.devices.dtos.CreateDeviceRequest;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.athos.agrocore.plantmonitor.Utils.saveIfNotNull;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;
    public Device createDevice(CreateDeviceRequest request){
        Device device = new Device();
        device.setDeviceUuid(request.deviceUid());
        device.setName(request.name());
        device.setHostname(request.hostName());
        device.setDeviceType(request.deviceType());
        return deviceRepository.save(device);
    }

    public Device findDeviceById(Long deviceId) {
        return getDeviceById(deviceId);
    }

    public List<Device> findDevicesByAuthenticatedUser(SecurityUser authenticatedUser) {
        return deviceRepository.getDeviceByUser_Id(authenticatedUser.getPersistentUser().getId());
    }




    private Device getDeviceById(Long deviceId) {
        return deviceRepository.getDeviceById(deviceId).orElseThrow(() -> new NotFoundException(Device.class, deviceId));
    }
    public Device getDeviceByUUID(String uuid, SecurityUser authenticatedUser) {
        // TODO: FIX THIS; HIGH PRIORITY
        //         return deviceRepository.getDeviceByDeviceUuid_AndUser_Id(uuid, authenticatedUser.getPersistentUser().getId()).orElseThrow(() -> new NotFoundException(Device.class, "uuid", uuid));
//        return deviceRepository.getDeviceByDeviceUuid_AndUser_Id(uuid, authenticatedUser.getPersistentUser().getId()).orElseThrow(() -> new NotFoundException(Device.class, "uuid", uuid));
        return deviceRepository.getDeviceByDeviceUuid(uuid).orElseThrow(() -> new NotFoundException(Device.class, "uuid", uuid));
    }


    public void deleteDeviceByIdFromAuthenticatedUser(Long deviceId, SecurityUser authenticatedUser) {
        Device device = getDeviceByIdFromAuthenticatedUser(deviceId, authenticatedUser);
        device.setUser(null);
        deviceRepository.save(device);
    }

    public Device getDeviceByIdFromAuthenticatedUser(Long deviceId,SecurityUser authenticatedUser) {
        return deviceRepository.findDeviceByIdAndUser_Id(deviceId, authenticatedUser.getPersistentUser().getId()).orElseThrow(() -> new NotFoundException(Device.class, deviceId));
    }


    public Device updateDevice(Long deviceId, UpdateDeviceRequest request, SecurityUser authenticatedUser) {
        Device device = getDeviceByIdFromAuthenticatedUser(deviceId, authenticatedUser);
        saveIfNotNull(request.name(), device::setName);
        saveIfNotNull(request.hostName(), device::setHostname);
        saveIfNotNull(request.deviceType(), device::setDeviceType);
        return deviceRepository.save(device);
    }


}
