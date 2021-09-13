package eu.magicmine.pivot.api.conversion.impl;

import eu.magicmine.pivot.api.conversion.Converter;

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
}
