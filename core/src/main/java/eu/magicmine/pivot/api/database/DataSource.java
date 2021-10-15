package eu.magicmine.pivot.api.database;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import eu.magicmine.pivot.api.utils.ConnectionData;

public abstract class DataSource extends AbstractModule {


    public abstract Injector openConnection(ConnectionData data);

    public abstract void close();

}
