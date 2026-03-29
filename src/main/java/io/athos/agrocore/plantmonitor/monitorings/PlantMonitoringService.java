package io.athos.agrocore.plantmonitor.monitorings;

import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.dtos.CreatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.UpdatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import io.athos.agrocore.plantmonitor.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.athos.agrocore.plantmonitor.ObjectHelperUtils.setIfNotNull;

@Service
public class PlantMonitoringService {
    @Autowired
    private PlantMonitoringRepository plantMonitoringRepository;
    public PlantMonitoring findPlantMonitoringById(Long id, SecurityUser authenticatedUser) {
        return plantMonitoringRepository.findById_AndUser_Id(id, authenticatedUser.getPersistentUser().getId()).orElseThrow(
                () -> new NotFoundException(PlantMonitoring.class, id)
        );
    }


    public PlantMonitoring createPlantMonitoring(CreatePlantMonitoringRequest request, SecurityUser authenticatedUser) {
        PlantMonitoring plantMonitoring = new PlantMonitoring();
        plantMonitoring.setUser(authenticatedUser.getPersistentUser());
        plantMonitoring.setCommonName(request.commonName());
        plantMonitoring.setSpecieName(request.specieName());
        return plantMonitoringRepository.save(plantMonitoring);
    }

    public PlantMonitoring updatePlantMonitoringId(Long plantMonitoringId, UpdatePlantMonitoringRequest request, SecurityUser authenticatedUser) {
        PlantMonitoring plantMonitoring = findPlantMonitoringById(plantMonitoringId, authenticatedUser);
        setIfNotNull(request.commonName(), plantMonitoring::setCommonName);
        setIfNotNull(request.specieName(), plantMonitoring::setSpecieName);
        return plantMonitoring;
    }

    public void deletePlantMonitoring(Long plantMonitoringId, SecurityUser authenticatedUser) {
        plantMonitoringRepository.deleteById_AndUser_Id(plantMonitoringId, authenticatedUser.getPersistentUser().getId());
    }
}
