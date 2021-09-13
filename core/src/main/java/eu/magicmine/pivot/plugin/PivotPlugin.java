package eu.magicmine.pivot.plugin;


import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.plugin.objects.StartupInformation;

public abstract class PivotPlugin {

    private Pivot pivot;

    public void startup(StartupInformation information, PivotServer server) {
        pivot = new Pivot(server,information.getLogger(),information.getConfiguration());
        pivot.onEnable();
    }

    public void shutdown() {
        pivot.onDisable();
    }

}
