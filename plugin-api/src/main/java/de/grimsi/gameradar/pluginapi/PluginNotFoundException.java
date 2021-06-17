package de.grimsi.gameradar.pluginapi;

public class PluginNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8692631101130464978L;

    public PluginNotFoundException() {
        super();
    }

    public PluginNotFoundException(String message) {
        super(message);
    }
}
