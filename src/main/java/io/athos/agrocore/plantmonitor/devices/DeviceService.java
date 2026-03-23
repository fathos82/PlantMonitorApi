package io.athos.agrocore.plantmonitor.devices;


import io.athos.agrocore.plantmonitor.devices.dtos.UpdateDeviceRequest;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.devices.dtos.CreateDeviceRequest;
import io.athos.agrocore.plantmonitor.users.User;
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

    public List<Device> findDevicesByUserId(Long userId) {
        return deviceRepository.getDeviceByUser_Id(userId);
    }




    private Device getDeviceById(Long deviceId) {
        return deviceRepository.getDeviceById(deviceId).orElseThrow(() -> new NotFoundException(Device.class, deviceId));
    }
    public Device getDeviceByUUID(String uuid) {
        return deviceRepository.getDeviceByDeviceUuid(uuid).orElseThrow(() -> new NotFoundException(Device.class, "uuid", uuid));
    }


    public Device updateDevice(Long deviceId,  UpdateDeviceRequest request) {
        Device device = getDeviceById(deviceId);
        saveIfNotNull(request.name(), device::setName);
        saveIfNotNull(request.hostName(), device::setHostname);
        saveIfNotNull(request.deviceType(), device::setDeviceType);
        return deviceRepository.save(device);
    }

    public void deleteDeviceForAuthenticatedUser(Long deviceId, User user) {
        Device device = getDeviceFromAuthenticatedUser(deviceId, user);
        device.setUser(null);
        deviceRepository.save(device);
    }

    private Device getDeviceFromAuthenticatedUser(Long deviceId, User user) {
        return deviceRepository.findDeviceByIdAndUser_Id(deviceId, user.getId()).orElseThrow(() -> new NotFoundException(Device.class, deviceId));
    }
}
