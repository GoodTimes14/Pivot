package eu.magicmine.pivot.api.conversion;


import java.util.List;

public interface Converter<T> {

    boolean canConvert(String str);

    T convert(String str);

    T nullValue();

    List<String> tabResult(String input);

}
