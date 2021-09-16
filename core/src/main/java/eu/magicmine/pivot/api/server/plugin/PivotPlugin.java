package eu.magicmine.pivot.api.server.plugin;

import eu.magicmine.pivot.api.commands.PivotCommand;

public abstract class PivotPlugin {


    public void registerCommands(PivotCommand... commands) {
        for(PivotCommand command : commands) {
            registerCommand(command);
        }
    }

    public abstract void registerCommand(PivotCommand command);


}
