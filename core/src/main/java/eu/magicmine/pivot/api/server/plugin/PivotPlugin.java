package eu.magicmine.pivot.api.server.plugin;

import eu.magicmine.pivot.api.commands.PivotCommand;

import java.util.Map;

public interface PivotPlugin {


    void registerCommands(PivotCommand... commands);
    void registerCommand(PivotCommand command);

    Map<String,Object> getConfigurationAsMap();

}
