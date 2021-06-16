package de.grimsi.gameradar.pluginapi;

import java.io.Serial;

public class PluginNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8692631101130464978L;

    public PluginNotFoundException() {
        super();
    }

    public PluginNotFoundException(String message) {
        super(message);
    }
}
