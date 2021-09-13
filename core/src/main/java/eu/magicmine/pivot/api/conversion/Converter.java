package eu.magicmine.pivot.api.conversion;


public interface Converter<T> {

    boolean canConvert(String str);

    T convert(String str);

}
