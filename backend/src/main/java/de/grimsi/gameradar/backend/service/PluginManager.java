package de.grimsi.gameradar.backend.service;

import de.grimsi.gameradar.pluginapi.GameRadarPlugin;
import de.grimsi.gameradar.pluginapi.PluginNotFoundException;
import org.apache.xbean.finder.ResourceFinder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PluginManager {

    @Autowired
    private Logger log;

    private final ResourceFinder pluginFinder = new ResourceFinder("META-INF/services");
    private List<? extends GameRadarPlugin> installedPlugins;
    private final Map<String, GameRadarPlugin> providerMap = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void loadInstalledPlugins() {

        log.debug("load all installed plugins");

        try {
            /* load the plugins */
            List<Class<? extends GameRadarPlugin>> plugins = pluginFinder.findAllImplementations(GameRadarPlugin.class);
            installedPlugins = instantiatePlugins(plugins);

            /* create a map to know which provider is handled by which plugin */
            installedPlugins.forEach(
                    plugin -> plugin.getMetaInfo().getSupportedProviders().forEach(
                            provider -> providerMap.put(provider, plugin)
                    )
            );

        } catch (IOException | ClassNotFoundException e) {
            throw new TypeNotPresentException(GameRadarPlugin.class.getName(), e);
        }
    }

    /**
     * Returns a list containing all installed plugins.
     *
     * @return list with all installed plugins.
     */
    List<GameRadarPlugin> getAllPlugins() {
        log.debug("get all plugins");
        return (List<GameRadarPlugin>) installedPlugins;
    }

    /**
     * Returns a list containing all installed plugins of a given type.
     *
     * @param pluginType The type of the plugin.
     * @return list with all installed plugins of given type.
     */
    <T extends GameRadarPlugin> List<T> getPluginsOfType(Class<T> pluginType) {
        log.debug("get all plugins of type '{}'", pluginType.getName());
        return getAllPlugins().stream()
                .filter(pluginType::isInstance)
                .map(p -> (T) p)
                .toList();
    }

    /**
     * Returns a plugin that supports a given provider.
     *
     * @param provider The provider that the plugin has to support.
     * @return The plugin that supports the given provider.
     */
    public GameRadarPlugin getPluginByProvider(String provider) {
        log.debug("get plugin for provider '{}'", provider);
        return providerMap.get(provider);
    }

    /**
     * Returns a plugin with a given type that supports a given provider.
     *
     * @param provider   The plugin provider.
     * @param pluginType The plugin type.
     * @return The plugin that supports the given provider and has the given type.
     */
    <T extends GameRadarPlugin> T getPluginByProvider(String provider, Class<T> pluginType) {
        log.debug("get plugin for provider '{}' with type '{}'", provider, pluginType.getName());
        GameRadarPlugin plugin = providerMap.get(provider);
        if (pluginType.isInstance(plugin)) {
            return (T) plugin;
        }
        throw new PluginNotFoundException("plugin.not-installed");
    }

    /**
     * Returns a plugin with a given name (according to the meta info of the plugin).
     *
     * @param pluginName The name of the plugin that should be returned.
     * @return The plugin with the given name.
     * @throws PluginNotFoundException in case no plugin with the given name could be found.
     */
    GameRadarPlugin getPlugin(String pluginName) {
        log.debug("get plugin with name '{}'", pluginName);
        return getAllPlugins().stream()
                .filter(p -> p.getMetaInfo().getName().equals(pluginName))
                .findFirst()
                .orElseThrow(() -> new PluginNotFoundException("No plugin with name " + pluginName + " installed."));
    }

    /**
     * Returns a plugin with a given name (according to the meta info of the plugin) and class.
     *
     * @param pluginName The name of the plugin that should be returned.
     * @return The plugin with the given name.
     * @throws PluginNotFoundException in case no plugin with the given name could be found.
     */
    <T extends GameRadarPlugin> T getPlugin(String pluginName, Class<T> pluginType) {
        log.debug("get plugin with name '{}' and type ' {}'", pluginName, pluginType.getName());
        return getPluginsOfType(pluginType).stream()
                .filter(p -> p.getMetaInfo().getName().equals(pluginName))
                .findFirst()
                .orElseThrow(() -> new PluginNotFoundException("No plugin with name " + pluginName + " and class '" + pluginType.getName() + "' installed."));
    }

    @SuppressWarnings("unchecked")
    private <T extends GameRadarPlugin> List<T> instantiatePlugins(List<Class<? extends T>> pluginClasses) {
        return (List<T>) pluginClasses.stream().map(
                p -> {
                    try {
                        log.debug("initialize plugin '{}'", p.getName());
                        return p.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new ClassCastException("Could not load plugin class '" + p.getName() + "'.");
                    }
                }
        ).toList();
    }
}
