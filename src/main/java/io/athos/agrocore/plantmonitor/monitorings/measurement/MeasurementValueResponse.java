package io.athos.agrocore.plantmonitor.monitorings.measurement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MeasurementValueResponse {
    // As estatísticas globais exatas (do banco)
    private Double min;
    private Double max;
    private Double avg;

    // A lista de pontos otimizada para o gráfico (LTTB)
    private List<MeasurementValueView> value;
}