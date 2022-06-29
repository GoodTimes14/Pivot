package eu.magicmine.pivot.api.utils.mongo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.conversions.Bson;

@Getter
@AllArgsConstructor
public class SavePair<T> {

    private Object identfier;
    private T saveObject;
    private Bson update;

    public boolean isUpdate() {
        return saveObject == null;
    }


}
