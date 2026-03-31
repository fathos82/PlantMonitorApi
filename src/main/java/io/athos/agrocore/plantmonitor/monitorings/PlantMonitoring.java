package io.athos.agrocore.plantmonitor.monitorings;

import io.athos.agrocore.plantmonitor.monitorings.measurement.Measurement;
import io.athos.agrocore.plantmonitor.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class PlantMonitoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String commonName;

    @Column(nullable = false)
    private String specieName;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Measurement> measurements;



}
