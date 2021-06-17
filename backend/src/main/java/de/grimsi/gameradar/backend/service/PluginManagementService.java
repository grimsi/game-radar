package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.backend.dto.PluginMetaInfoDto;
import de.grimsi.gameradar.pluginapi.GameRadarPlugin;
import de.grimsi.gameradar.pluginapi.PluginMetaInfo;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PluginManagementService {

    @Autowired
    private Logger log;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PluginManager pluginManager;


    public List<PluginMetaInfoDto> getAllPluginMetaInfo() {

        log.debug("get all plugin meta info");

        List<PluginMetaInfo> pluginMetaInfos = pluginManager.getAllPlugins().stream()
                .map(GameRadarPlugin::getMetaInfo)
                .toList();

        return convertToDto(pluginMetaInfos);
    }

    public List<String> getAllSupportedProviders() {

        log.debug("get all plugin supported providers");

        return pluginManager.getAllPlugins().stream()
                .map(plugin -> plugin.getMetaInfo().getSupportedProviders())
                .flatMap(List::stream)
                .toList();
    }

    private PluginMetaInfoDto convertToDto(PluginMetaInfo pluginMetaInfo) {
        return mapper.map(pluginMetaInfo, PluginMetaInfoDto.class);
    }

    private List<PluginMetaInfoDto> convertToDto(List<PluginMetaInfo> pluginMetaInfos) {
        return pluginMetaInfos.stream().map(this::convertToDto).toList();
    }
}
