package io.athos.agrocore.plantmonitor.monitorings.measurement;

import java.io.Serializable;
import java.time.Instant;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MeasurementValuePK implements Serializable {
    private Long measurementParentId;
    private Instant timestamp;
}