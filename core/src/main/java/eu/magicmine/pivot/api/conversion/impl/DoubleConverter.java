package eu.magicmine.pivot.api.conversion.impl;

import eu.magicmine.pivot.api.conversion.Converter;

import java.util.ArrayList;
import java.util.List;

public class DoubleConverter implements Converter<Double> {

    @Override
    public boolean canConvert(String str) {
        try {
            double x = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }

    }

    @Override
    public Double convert(String str) {
        return Double.parseDouble(str);
    }

    @Override
    public Double nullValue() {
        return 0d;
    }

    @Override
    public List<String> tabResult(String input) {
        return new ArrayList<>();
    }
}
