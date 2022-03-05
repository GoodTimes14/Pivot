package eu.magicmine.pivot.api.database;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import eu.magicmine.pivot.api.database.provider.DataProvider;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class DataSource extends AbstractModule {

    private final Map<String, List<DataProvider>> loadedProviders = new HashMap<>();

    public abstract Injector openConnection(ConnectionData data);

    public void close() {
        for(List<DataProvider> providers : loadedProviders.values()) {
            for (DataProvider provider : providers) {
                provider.close();
            }
        }
    }

}
