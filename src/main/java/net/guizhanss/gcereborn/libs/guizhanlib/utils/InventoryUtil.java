package net.guizhanss.gcereborn.libs.guizhanlib.utils;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Java-8 port of GuizhanLib's {@code InventoryUtil} - see the note on
 * {@link net.guizhanss.gcereborn.libs.guizhanlib.commands.AbstractCommand}.
 *
 * @author ybw0014 (original), downleveled for Java 8
 */
@SuppressWarnings("ConstantConditions")
@UtilityClass
public final class InventoryUtil {

    @ParametersAreNonnullByDefault
    public static void push(Player p, ItemStack... itemStacks) {
        Preconditions.checkArgument(p != null, "player should not be null");

        push(p, p.getLocation(), itemStacks);
    }

    @ParametersAreNonnullByDefault
    public static void push(Player p, Location loc, ItemStack... itemStacks) {
        Preconditions.checkArgument(p != null, "player should not be null");
        Preconditions.checkArgument(loc != null, "location should not be null");
        Preconditions.checkArgument(itemStacks != null, "at least one ItemStack is required");
        Preconditions.checkArgument(itemStacks.length > 0, "at least one ItemStack is required");

        itemStacks = Arrays.stream(itemStacks).filter(Objects::nonNull).toArray(ItemStack[]::new);

        Map<Integer, ItemStack> remainingItemMap = p.getInventory().addItem(itemStacks);

        for (ItemStack item : remainingItemMap.values()) {
            p.getWorld().dropItem(loc, item.clone());
        }
    }
}
