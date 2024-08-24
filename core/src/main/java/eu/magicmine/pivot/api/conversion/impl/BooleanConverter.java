package eu.magicmine.pivot.api.conversion.impl;

import eu.magicmine.pivot.api.conversion.Converter;

import java.util.Arrays;
import java.util.List;

public class BooleanConverter implements Converter<Boolean> {

    @Override
    public boolean canConvert(String str) {
        return true;
    }

    @Override
    public Boolean convert(String str) {
        return Boolean.parseBoolean(str);
    }

    @Override
    public Boolean nullValue() {
        return false;
    }

    @Override
    public List<String> tabResult(String input) {
        return Arrays.asList("true","false");
    }
}
