package net.guizhanss.gcereborn.libs.guizhanlib.commands;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The {@link BaseCommand} is the root node of a command tree.
 * Java-8 port of GuizhanLib's {@code BaseCommand} - see the note on {@link AbstractCommand}.
 *
 * @author ybw0014 (original), downleveled for Java 8
 */
@Getter
public abstract class BaseCommand extends AbstractCommand implements CommandExecutor, TabCompleter {

    private final PluginCommand command;

    @ParametersAreNonnullByDefault
    protected BaseCommand(PluginCommand command, BiFunction<AbstractCommand, CommandSender, String> description, String usage,
                          SubCommand... subCommands) {
        super(command.getName(), description, usage, subCommands);
        this.command = command;
    }

    @Override
    @ParametersAreNonnullByDefault
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        onCommandExecute(sender, command, label, args);
        return true;
    }

    @Override
    @Nullable
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return onTabCompleteExecute(sender, args);
    }

    /**
     * Register this command.
     */
    public void register() {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }
}
