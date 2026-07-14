package net.guizhanss.gcereborn.libs.guizhanlib.commands;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * This is a node in a command tree. A node can have 0-1 parent node and 0-n child nodes.
 * <p>
 * Java-8 port of GuizhanLib's {@code AbstractCommand}. GuizhanLib-api's compiled artifact is
 * class-file version 60 (Java 16), which a Java-8 {@code javac} cannot read as a compile
 * dependency at all ("class file has wrong version 60.0, should be 52.0") - so the addon's own
 * command framework (this + {@link BaseCommand}/{@link SubCommand}/{@link Usage}) is vendored as
 * plain Java-8 source instead of depending on the upstream jar. This also sidesteps any risk of the
 * jar's other classes (e.g. {@code AbstractAddon}) referencing the pre-fork
 * {@code io.github.thebusybiscuit.slimefun4} API.
 *
 * @author ybw0014 (original), downleveled for Java 8
 */
@SuppressWarnings("ConstantConditions")
@Getter
public abstract class AbstractCommand {

    private final Set<SubCommand> subCommands = new HashSet<>();
    private final AbstractCommand parent;
    private final String name;
    private final BiFunction<AbstractCommand, CommandSender, String> description;
    private final Usage usage;

    protected AbstractCommand(@Nullable AbstractCommand parent, @Nonnull String name,
                              @Nonnull BiFunction<AbstractCommand, CommandSender, String> description,
                              @Nonnull String usage, @Nonnull SubCommand... subCommands) {
        Preconditions.checkArgument(name != null && !name.isEmpty(), "name cannot be null or empty");
        Preconditions.checkArgument(description != null, "description cannot be null");

        this.parent = parent;
        this.name = name;
        this.description = description;
        this.usage = new Usage(usage);
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    @ParametersAreNonnullByDefault
    protected AbstractCommand(String name, BiFunction<AbstractCommand, CommandSender, String> description,
                              String usage, SubCommand... subCommands) {
        this(null, name, description, usage, subCommands);
    }

    @Nonnull
    public AbstractCommand addSubCommand(@Nonnull SubCommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
        return this;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasSubCommands() {
        return !subCommands.isEmpty();
    }

    @ParametersAreNonnullByDefault
    protected final void onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (!hasSubCommands()) {
            if (getUsage().isValid(args)) {
                onExecute(sender, args);
            } else {
                sendHelp(sender, label);
            }
        } else {
            if (args.length == 0) {
                sendHelp(sender, label);
            } else {
                for (SubCommand subCommand : getSubCommands()) {
                    if (subCommand.getName().equalsIgnoreCase(args[0])) {
                        subCommand.onCommandExecute(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                        return;
                    }
                }
                sendHelp(sender, label);
            }
        }
    }

    @Nullable
    @ParametersAreNonnullByDefault
    protected final List<String> onTabCompleteExecute(CommandSender sender, String[] args) {
        if (hasSubCommands()) {
            if (args.length == 1) {
                return getSubCommands().stream().map(AbstractCommand::getName).collect(Collectors.toList());
            } else {
                for (SubCommand subCommand : getSubCommands()) {
                    if (subCommand.getName().equalsIgnoreCase(args[0])) {
                        return subCommand.onTabCompleteExecute(sender, Arrays.copyOfRange(args, 1,
                            args.length));
                    }
                }
                return Collections.emptyList();
            }
        } else {
            return onTab(sender, args);
        }
    }

    @Nonnull
    public String getFullUsage(@Nonnull String label) {
        final Deque<AbstractCommand> layers = new ArrayDeque<>();
        AbstractCommand current = this;
        while (current != null) {
            layers.push(current);
            current = current.getParent();
        }
        String cmd = layers.stream()
            .map(command -> command instanceof BaseCommand ? label : command.getName())
            .collect(Collectors.joining(" ", "/", ""));
        cmd += " " + getUsage().get();
        return cmd;
    }

    @ParametersAreNonnullByDefault
    protected void sendHelp(CommandSender sender, String label) {
        if (hasSubCommands()) {
            for (SubCommand subCommand : getSubCommands()) {
                subCommand.sendHelp(sender, label);
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + getFullUsage(label) + ChatColor.WHITE + " - " + getDescription().apply(this, sender));
        }
    }

    @ParametersAreNonnullByDefault
    public abstract void onExecute(CommandSender sender, String[] args);

    @ParametersAreNonnullByDefault
    public List<String> onTab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
