package io.athos.agrocore.plantmonitor.monitorings;

import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.monitorings.dtos.CreatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.dtos.UpdatePlantMonitoringRequest;
import io.athos.agrocore.plantmonitor.monitorings.measurement.Measurement;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementRepository;
import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementService;
import io.athos.agrocore.plantmonitor.security.SecurityUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.athos.agrocore.plantmonitor.ObjectHelperUtils.setIfNotNull;

@Service
public class PlantMonitoringService {
    @Autowired
    private PlantMonitoringRepository plantMonitoringRepository;
    @Autowired
    private MeasurementService measurementService;

    public PlantMonitoring findById(Long id, SecurityUser authenticatedUser) {
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
        PlantMonitoring plantMonitoring = findById(plantMonitoringId, authenticatedUser);
        setIfNotNull(request.commonName(), plantMonitoring::setCommonName);
        setIfNotNull(request.specieName(), plantMonitoring::setSpecieName);
        return plantMonitoring;
    }

    @Transactional
    public void deletePlantMonitoring(Long plantMonitoringId, SecurityUser authenticatedUser) {
        PlantMonitoring plant = findById(plantMonitoringId, authenticatedUser);
        for (Measurement measurement : plant.getMeasurements()) {
            measurementService.deleteMeasurement(measurement.getId(),  authenticatedUser);
        }
        plantMonitoringRepository.delete(plant);
    }
    @Transactional
    public List<PlantMonitoring> findAll(SecurityUser authenticatedUser) {
        return plantMonitoringRepository.findAllByUser_Id(authenticatedUser.getPersistentUser().getId());
    }
}
