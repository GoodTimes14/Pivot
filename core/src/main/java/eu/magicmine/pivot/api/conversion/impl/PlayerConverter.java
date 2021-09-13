package eu.magicmine.pivot.api.conversion.impl;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.conversion.Converter;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.utils.PivotHolder;

public class PlayerConverter extends PivotHolder implements Converter<PivotPlayer> {

    public PlayerConverter(Pivot pivot) {
        super(pivot);
    }

    @Override
    public boolean canConvert(String str) {
        return true;
    }

    @Override
    public PivotPlayer convert(String str) {
        return pivot.getServer().getPlayer(str).orElse(null);
    }
}
