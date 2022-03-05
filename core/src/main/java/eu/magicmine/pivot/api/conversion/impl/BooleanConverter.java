package eu.magicmine.pivot.api.conversion.impl;

import eu.magicmine.pivot.api.conversion.Converter;

public class BooleanConverter implements Converter<Boolean> {
    @Override
    public boolean canConvert(String str) {
        return true;
    }

    @Override
    public Boolean convert(String str) {
        return Boolean.parseBoolean(str);
    }
}
