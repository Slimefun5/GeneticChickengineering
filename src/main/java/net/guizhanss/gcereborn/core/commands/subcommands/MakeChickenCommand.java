package net.guizhanss.gcereborn.core.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.guizhanss.gcereborn.libs.guizhanlib.commands.AbstractCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.commands.AbstractSubCommand;
import net.guizhanss.gcereborn.core.genetics.DNA;
import net.guizhanss.gcereborn.util.Messages;
import net.guizhanss.gcereborn.utils.ChickenUtils;
import net.guizhanss.gcereborn.libs.guizhanlib.utils.InventoryUtil;

public final class MakeChickenCommand extends AbstractSubCommand implements DnaCompletion {

    public MakeChickenCommand(@Nonnull AbstractCommand parent) {
        super(parent, "makechicken", "<DNA> [baby]");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            Messages.send(sender, ChatColor.RED, "command.no-permission", "You do not have permission!");
            return;
        }
        if (!(sender instanceof Player)) {
            Messages.send(sender, ChatColor.RED, "command.no-console", "You must be a player to do this!");
            return;
        }
        Player p = (Player) sender;

        String notation = args[0];
        if (!DNA.isValidSequence(notation)) {
            Messages.send(sender, ChatColor.RED, "command.invalid-dna-notation", "Invalid DNA notation: {0}", notation);
            return;
        }
        boolean isBaby = true;
        if (args.length == 2) {
            isBaby = Boolean.parseBoolean(args[1]);
        }

        DNA dna = new DNA(notation.toCharArray());
        ItemStack chicken = ChickenUtils.fromDNA(dna, isBaby);
        InventoryUtil.push(p, chicken);
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("true", "false");
        }
        return tabComplete(sender, args, 0);
    }
}
