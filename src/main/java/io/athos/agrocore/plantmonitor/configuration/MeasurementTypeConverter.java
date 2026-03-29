package io.athos.agrocore.plantmonitor.configuration;

import io.athos.agrocore.plantmonitor.monitorings.measurement.MeasurementType;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class MeasurementTypeConverter
        implements Converter<String, MeasurementType> {

    @Override
    public MeasurementType convert(String source) {
        return MeasurementType.fromString(source);
    }

    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
}
