package eu.magicmine.pivot.api.conversion.impl;

import eu.magicmine.pivot.api.conversion.Converter;

import java.util.ArrayList;
import java.util.List;

public class IntegerConverter implements Converter<Integer> {


    @Override
    public boolean canConvert(String str) {
        try {
          int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }

    }

    @Override
    public Integer convert(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Integer nullValue() {
        return 0;
    }

    @Override
    public List<String> tabResult(String input) {
        return new ArrayList<>();
    }
}
