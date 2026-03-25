package io.athos.agrocore.plantmonitor.devices.sensors;

import io.athos.agrocore.plantmonitor.devices.DeviceService;
import io.athos.agrocore.plantmonitor.devices.sensors.dtos.RegisterSensorRequest;
import io.athos.agrocore.plantmonitor.devices.sensors.dtos.UpdateSensorRequest;
import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    public VirtualSensor getSensorById(Long sensorId) {
        return virtualSensorRepository.findById(sensorId)
                .orElseThrow(() -> new NotFoundException(VirtualSensor.class, sensorId));
    }


    public VirtualSensor createSensor(RegisterSensorRequest request) {
        System.out.println("REGISTERING SENSOR");

        VirtualSensor virtualSensor = new VirtualSensor();
        virtualSensor.setName(request.sensorName());
        virtualSensor.setDevice(deviceService.getDeviceByUUID(request.deviceUid()));
        virtualSensor.setParameters(request.parameters());
        virtualSensor.setModel(request.model());
//        virtualSensor.setCapabilities(new HashSet<>(request.capabilities()));
        System.out.println("REGISTERING SENSOR ");

        return virtualSensorRepository.save(virtualSensor);
    }

    public VirtualSensor updateSensor(Long sensorId, UpdateSensorRequest request) {
        VirtualSensor virtualSensor = getSensorById(sensorId);
        virtualSensor.setName(request.sensorName());
        virtualSensor.setParameters(request.parameters());
        // todo: set Parameters and model
        return virtualSensorRepository.save(virtualSensor);
    }

    public void deleteSensor(Long sensorId) {
        virtualSensorRepository.deleteById(sensorId);
    }
    public  List<VirtualSensor> listSensorByDiveUuid(String deviceUuId) {
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

    public Page<SensorNotify> listError(Long sensorId, Pageable pageable) {
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
}
