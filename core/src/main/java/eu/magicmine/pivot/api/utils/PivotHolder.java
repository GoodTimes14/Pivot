package eu.magicmine.pivot.api.utils;

import eu.magicmine.pivot.Pivot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class PivotHolder {

    public final Pivot pivot;

}
