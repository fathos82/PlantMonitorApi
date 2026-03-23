package io.athos.agrocore.plantmonitor.monitorings;

import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.dtos.CreatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.UpdatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.users.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlantMonitoringService {
    @Autowired
    private PlantMonitoringRepository plantMonitoringRepository;

    // TODO: TEMPORARIO
    @Autowired
    private UserService userService;
    public PlantMonitoring findPlantMonitoringById(Long id) {
        return plantMonitoringRepository.findById(id).orElseThrow(
                () -> new NotFoundException(PlantMonitoring.class, id)
        );
    }


    public PlantMonitoring createPlantMonitoring(CreatePlantMonitoringRequest request) {
        // TODO: Get Authenticated USER
        PlantMonitoring plantMonitoring = new PlantMonitoring();
        System.out.println(request.userId());

        plantMonitoring.setUser(userService.getUserById(request.userId()));


        plantMonitoring.setCommonName(request.commonName());
        plantMonitoring.setSpecieName(request.specieName());
        return plantMonitoringRepository.save(plantMonitoring);
    }

    public PlantMonitoring updatePlantMonitoringId(Long plantMonitoringId, UpdatePlantMonitoringRequest request) {
        PlantMonitoring plantMonitoring = findPlantMonitoringById(plantMonitoringId);
        // todo: continue
        return plantMonitoring;
    }

    public void deletePlantMonitoring(Long plantMonitoringId) {
        plantMonitoringRepository.deleteById(plantMonitoringId);
    }
}
