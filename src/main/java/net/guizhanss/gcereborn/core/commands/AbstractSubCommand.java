package net.guizhanss.gcereborn.core.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.libs.guizhanlib.commands.AbstractCommand;
import net.guizhanss.gcereborn.libs.guizhanlib.commands.SubCommand;
import net.guizhanss.gcereborn.util.Messages;

import lombok.Getter;

@Getter
public abstract class AbstractSubCommand extends SubCommand {

    protected AbstractSubCommand(@Nullable AbstractCommand parent, @Nonnull String name, @Nonnull String usage, SubCommand... subCommands) {
        super(parent, name, (cmd, sender) -> getDescription(name, sender), usage, subCommands);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    private static String getDescription(String name, CommandSender sender) {
        if ("calcchance".equals(name)) {
            return Messages.get(sender, "command.calcchance-description",
                    "Calculate the chance of getting a chicken with the given 2 parent DNAs and child DNA.");
        }
        if ("makechicken".equals(name)) {
            return Messages.get(sender, "command.makechicken-description",
                    "Create a chicken item with the given DNA.");
        }
        return GeneticChickengineering.getLocalization().getString("commands." + name + ".description");
    }

    protected boolean hasPermission(@Nonnull CommandSender sender) {
        return sender.hasPermission("geneticchickengineering.command." + getName());
    }
}
