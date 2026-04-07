package io.athos.agrocore.plantmonitor.monitorings.measurement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.Instant;


// TODO:
//@SequenceGenerator(
//        name = "measurement_value_seq",
//        sequenceName = "measurement_value_seq",
//        allocationSize = 50 // 🔥 importante para batch insert
//)
//@GeneratedValue(
//        strategy = GenerationType.SEQUENCE,
//        generator = "measurement_value_seq"
//)
// PARTITION BY RANGE (timestamp)





@Setter
@Getter
@NoArgsConstructor
// 2. A Entidade perde o ID autoincremental
@Entity
@Table(
        name = "measurement_value",
        indexes = {
                @Index(
                        name = "idx_measurement_parent_time_desc",
                        columnList = "measurement_parent_id, timestamp DESC"
                )
        }
)



@IdClass(MeasurementValuePK.class)
public class MeasurementValue implements Persistable<MeasurementValuePK> {


    @Id
    @JoinColumn(name = "measurement_parent_id", insertable = false, updatable = false)
    private Long measurementParentId;
    @Id
    private Instant timestamp;

    @Column(nullable = false)
    private Double value;
    @Override
    public MeasurementValuePK getId() {
        return new MeasurementValuePK(measurementParentId, timestamp);
    }

    @Override
    public boolean isNew() {
        return true; // Força o Hibernate a dar apenas o INSERT direto
    }
    public MeasurementValue(Measurement measurementParent, Instant timestamp, double value) {
        this.measurementParentId = measurementParent.getId();
        this.timestamp = timestamp;
        this.value = value;
    }
}
