package de.grimsi.gameradar.pluginapi;

public record PluginMetaInfo(
        String name,
        Class<? extends GameRadarPlugin> type,
        String version,
        String author,
        String description,
        String supportedProviders,
        String license,
        String repositoryUrl
) {}
