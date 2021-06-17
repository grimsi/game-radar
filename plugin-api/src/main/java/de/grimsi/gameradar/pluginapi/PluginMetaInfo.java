package de.grimsi.gameradar.pluginapi;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class PluginMetaInfo {

    @NonNull
    private String name;

    @NonNull
    private Class<? extends GameRadarPlugin> type;

    @NonNull
    private String version;

    @NonNull
    private String author;

    @NonNull
    private String description;

    @NonNull
    private List<String> supportedProviders;

    private String license;

    private String repositoryUrl;
}
