package eu.magicmine.pivot.api.commands.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommandResult {

    private final ResultType type;
    private final String message;

    public enum ResultType {
        ERROR,SUCCESS
    }

}
