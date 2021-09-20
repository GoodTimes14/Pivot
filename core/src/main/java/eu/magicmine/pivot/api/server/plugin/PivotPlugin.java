package eu.magicmine.pivot.api.server.plugin;

import eu.magicmine.pivot.api.commands.PivotCommand;

public interface PivotPlugin {


    void registerCommands(PivotCommand... commands);
    void registerCommand(PivotCommand command);


}
