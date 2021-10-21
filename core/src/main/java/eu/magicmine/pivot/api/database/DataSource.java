package eu.magicmine.pivot.api.database;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import eu.magicmine.pivot.api.database.provider.DataProvider;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class DataSource extends AbstractModule {

    private final Map<String, DataProvider> loadedProviders = new HashMap<>();

    public abstract Injector openConnection(ConnectionData data);

    public void close() {
        for(DataProvider service : loadedProviders.values()) {
            service.close();
        }
    }

}
