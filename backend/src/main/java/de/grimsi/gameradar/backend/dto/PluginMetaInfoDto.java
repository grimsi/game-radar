package de.grimsi.gameradar.backend.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.grimsi.gameradar.pluginapi.GameRadarPlugin;
import lombok.Data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Data
public class PluginMetaInfoDto {
    private String author;
    private String name;
    private String version;

    @JsonSerialize(using = ClassToStringSerializer.class)
    private Class<? extends GameRadarPlugin> type;

    private String description;
    private Set<String> supportedProviders = new HashSet<>();
    private String license;
    private String repositoryUrl;

    private static class ClassToStringSerializer extends JsonSerializer<Class<? extends GameRadarPlugin>> {
        @Override
        public void serialize(Class aClass, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(aClass.getSimpleName());
        }
    }
}
