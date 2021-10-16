package eu.magicmine.pivot.api.utils.mongo;

import lombok.Data;
import org.bson.conversions.Bson;

@Data
public class DataUpdate {

    private final Object identifier;
    private final Bson update;


}
