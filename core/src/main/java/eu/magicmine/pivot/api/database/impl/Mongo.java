package eu.magicmine.pivot.api.database.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import eu.magicmine.pivot.api.database.DataSource;
import eu.magicmine.pivot.api.utils.ConnectionData;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Getter
public class Mongo extends DataSource {

    private MongoClient client;

    @Override
    public Injector openConnection(ConnectionData data) {
        ServerAddress serverAddress = new ServerAddress(data.getHost(),data.getPort());
        MongoCredential credential = data.isAuth() ? MongoCredential.createCredential(data.getUsername(),data.getDatabase(),data.getPassword().toCharArray()) : null;
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(pojoCodecRegistry).uuidRepresentation(UuidRepresentation.STANDARD).build();
        client = credential != null ? new MongoClient(serverAddress, credential, options) : new MongoClient(serverAddress,options);
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(MongoClient.class).annotatedWith(Names.named("client")).toInstance(client);
    }

    @Override
    public void close() {
        client.close();
    }
}