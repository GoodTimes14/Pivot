package eu.magicmine.pivot.api.conversion.manager;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.conversion.Converter;
import eu.magicmine.pivot.api.conversion.impl.DoubleConverter;
import eu.magicmine.pivot.api.conversion.impl.IntegerConverter;
import eu.magicmine.pivot.api.conversion.impl.PlayerConverter;
import eu.magicmine.pivot.api.utils.PivotHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConversionManager extends PivotHolder {

    public Map<Class<?>, Converter<?>> converterMap;

    public ConversionManager(Pivot pivot) {
        super(pivot);
        converterMap = new HashMap<>();
        load();
    }

    public void load() {
        registerConverter(int.class, new IntegerConverter());
        registerConverter(double.class, new DoubleConverter());
        registerConverter(pivot.getServer().getPlayerClass(),new PlayerConverter(pivot));
    }

    public void registerConverter(Class<?> clazz,Converter<?> converter) {
        converterMap.put(clazz, converter);
    }

    public Optional<Converter<?>> getConverter(Class<?> clazz) {
        return converterMap.containsKey(clazz) ? Optional.of(converterMap.get(clazz)) : Optional.empty();
    }

}
