package io.athos.agrocore.plantmonitor.devices.sensors;

import io.athos.agrocore.plantmonitor.devices.Device;
import io.athos.agrocore.plantmonitor.devices.DeviceService;
import io.athos.agrocore.plantmonitor.devices.sensors.dtos.RegisterSensorRequest;
import io.athos.agrocore.plantmonitor.devices.sensors.dtos.UpdateSensorRequest;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementService;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static io.athos.agrocore.plantmonitor.devices.sensors.SensorNotify.NotifyType.SENSOR_ERROR;

@Service
public class SensorService {
    @Autowired
    private VirtualSensorRepository virtualSensorRepository;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SensorNotifyRepository sensorNotifyRepository;
    @Autowired
    private SensorTemplateRepository sensorTemplateRepository;
    @Autowired
    private MeasurementService measurementService;

    public VirtualSensor getSensorByIdAndAuthenticatedUser(Long sensorId, SecurityUser authenticatedUser) {
        return virtualSensorRepository.findById_AndDevice_User_Id(sensorId, authenticatedUser.getPersistentUser().getId())
                .orElseThrow(() -> new NotFoundException(VirtualSensor.class, sensorId));
    }

    public VirtualSensor getSensorById(Long sensorId) {
        return virtualSensorRepository.findById(sensorId)
                .orElseThrow(() -> new NotFoundException(VirtualSensor.class, sensorId));
    }



    public VirtualSensor createSensor(RegisterSensorRequest request, SecurityUser authenticatedUser) {
        Device device = deviceService.getDeviceByIdFromAuthenticatedUser(request.deviceId(), authenticatedUser);
        SensorTemplate sensorTemplate = sensorTemplateRepository.findById(request.sensorTemplateId())
                .orElseThrow(() -> new  NotFoundException(SensorTemplate.class, request.sensorTemplateId()));
        VirtualSensor sensor = new VirtualSensor(sensorTemplate, device, request.parameters());
        return virtualSensorRepository.save(sensor);
    }

    public VirtualSensor updateSensor(Long sensorId, UpdateSensorRequest request, SecurityUser authenticatedUser) {
        VirtualSensor virtualSensor = getSensorByIdAndAuthenticatedUser(sensorId, authenticatedUser);
        virtualSensor.setParameters(request.parameters());
        return virtualSensorRepository.save(virtualSensor);
    }

    @Transactional
    public void deleteSensor(Long sensorId, SecurityUser authenticatedUser) {
        measurementService.detachSensorFromMeasurements(sensorId,authenticatedUser);
        virtualSensorRepository.deleteById_AndDevice_User_Id(sensorId, authenticatedUser.getPersistentUser().getId());
    }
    public  List<VirtualSensor> listSensorByDeviceUuid(String deviceUuId) {
        return virtualSensorRepository.findAllByDevice_DeviceUuid(deviceUuId);

    }

    public void registerError(Long sensorId, String message) {
        VirtualSensor virtualSensor = getSensorById(sensorId);
        virtualSensor.setHasError(true);
        SensorNotify sensorNotify = new SensorNotify();
        sensorNotify.setMessage(message);
        sensorNotify.setNotifyType(SENSOR_ERROR);
        sensorNotify.setSensor(virtualSensor);
        sensorNotifyRepository.save(sensorNotify);
        virtualSensorRepository.save(virtualSensor);
    }


    public Page<SensorNotify> listError(Long sensorId, Pageable pageable, SecurityUser authenticatedUser) {
        return sensorNotifyRepository.findBySensorIdAndNotifyType(sensorId, SENSOR_ERROR, pageable);
    }

    public void updateTimestamp(VirtualSensor virtualSensor) {
        if(virtualSensor.isHasError()) {
            virtualSensor.setHasError(false);
        }
        virtualSensor.onDataReceived();
        virtualSensorRepository.save(virtualSensor);
    }

    public void activateSensor(Long sensorId) {
        VirtualSensor virtualSensor = getSensorById(sensorId);
        updateTimestamp(virtualSensor);
    }

    public List<SensorTemplate> listTemplates() {
        return sensorTemplateRepository.findAll();
    }

    public List<VirtualSensor> listSensorByDeviceId(@Valid Long deviceId, SecurityUser authenticatedUser) {
        return virtualSensorRepository.findAllByDevice_IdAndDevice_User_Id(deviceId, authenticatedUser.getPersistentUser().getId());
    }
}
