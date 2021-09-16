package eu.magicmine.pivot.velocity.test;

import com.velocitypowered.api.proxy.Player;
import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.annotation.CommandInfo;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.velocity.command.PivotVelocityCommand;
import net.kyori.adventure.text.Component;

@CommandInfo(name = "test",description = "Test command",permission = "magicmine.admin",aliases = "t")
public class TestCommand extends PivotVelocityCommand {

    public TestCommand(Pivot pivot) {
        super(pivot);
    }

    @DefaultCommand
    public void defaultCommand(Player player) {
        player.sendMessage(Component.text("Culo"));
    }

    @SubCommand(name = "prova",description = "Test comando prova dio")
    public void argTest(Player sender, @Argument(name = "to") Player to, @Argument(name = "amount") int cazzo) {
        sender.sendMessage(Component.text("Culo: " + to.getUniqueId() + " " + cazzo));

    }


    @Override
    public String noPermsMessage() {
        return "§cNo permissions.";
    }
}
