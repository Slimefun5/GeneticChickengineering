package net.guizhanss.gcereborn.items.common;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun5.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun5.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun5.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun5.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun5.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun5.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun5.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun5.libraries.dough.protection.Interaction;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.utils.ChickenUtils;
import net.guizhanss.gcereborn.utils.CompatUtils;

public class ChickenNet extends SimpleSlimefunItem<EntityInteractHandler> implements NotPlaceable {

    public ChickenNet(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(getItemUsehandler());
    }

    @Override
    @Nonnull
    public EntityInteractHandler getItemHandler() {
        return (e, item, offHand) -> {
            if (e.getRightClicked().getType() != EntityType.CHICKEN) {
                return;
            }
            Chicken chicken = (Chicken) e.getRightClicked();

            if (!Slimefun.getProtectionManager().hasPermission(e.getPlayer(), chicken.getLocation(), Interaction.INTERACT_ENTITY)) {
                GeneticChickengineering.getLocalization().sendMessage(e.getPlayer(), "no-permission");
                return;
            }

            Location l = CompatUtils.centerLocation(chicken.getLocation());
            ItemStack pocketChicken = ChickenUtils.capture(chicken);
            l.getWorld().dropItemNaturally(l, pocketChicken);
            CompatUtils.playSound(l, "ENTITY_CHICKEN_EGG", 1F, 1F);
        };
    }

    @Nonnull
    public ItemUseHandler getItemUsehandler() {
        return PlayerRightClickEvent::cancel;
    }
}
