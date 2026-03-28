package io.athos.agrocore.plantmonitor.monitorings.measurement;

import java.time.Instant;

public interface MeasurementValueView {
    double getValue();
    Instant getTimestamp();

}