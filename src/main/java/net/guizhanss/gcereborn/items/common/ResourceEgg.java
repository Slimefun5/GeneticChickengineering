package net.guizhanss.gcereborn.items.common;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun5.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun5.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun5.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun5.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun5.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun5.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun5.libraries.dough.protection.Interaction;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.genetics.DNA;
import net.guizhanss.gcereborn.items.GCEItems;
import net.guizhanss.gcereborn.utils.ChickenUtils;
import net.guizhanss.gcereborn.utils.CompatUtils;

public class ResourceEgg extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private final Material resource;
    private final boolean allowInNether;

    public ResourceEgg(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, Material resource,
                       boolean allowInNether) {
        super(itemGroup, item, recipeType, makeRecipe(resource));
        this.resource = resource;
        this.allowInNether = allowInNether;
        setGuideType("resources");
    }

    @Nonnull
    private static ItemStack[] makeRecipe(@Nonnull Material resource) {
        ItemStack[] recipe = new ItemStack[9];
        ItemStack fake = GCEItems.POCKET_CHICKEN.clone().item();
        DNA dna;
        if (resource == Material.WATER) {
            dna = new DNA(62);
        } else {
            dna = new DNA(41);
        }
        ChickenUtils.setPocketChicken(fake, null, dna);
        recipe[4] = fake;
        return recipe;
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();
            Optional<Block> block = e.getClickedBlock();
            if (!block.isPresent()) {
                return;
            }
            Block b = block.get();
            Block place = b.getRelative(e.getClickedFace());
            if (!Slimefun.getProtectionManager().hasPermission(e.getPlayer(), place.getLocation(), Interaction.PLACE_BLOCK)) {
                GeneticChickengineering.getLocalization().sendMessage(e.getPlayer(), "no-permission");
                return;
            }
            // Block#isReplaceable() postdates the fork's Java-8-compatible spigot-api compile baseline
            // (1.16.5); air-or-liquid is a close approximation of the original "replaceable" check.
            if (place.getType().isAir() || place.isLiquid()) {
                if (resource == Material.WATER && !allowInNether && place.getWorld().getEnvironment() == World.Environment.NETHER) {
                    CompatUtils.spawnParticle(place.getLocation().add(0.5, 0, 0.5), "CLOUD", 5);
                    CompatUtils.playSound(CompatUtils.centerLocation(place.getLocation()), "BLOCK_LAVA_EXTINGUISH", 1F, 1F);
                } else {
                    place.setType(resource);
                }

                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    ItemUtils.consumeItem(e.getItem(), false);
                }
            }
        };
    }
}
