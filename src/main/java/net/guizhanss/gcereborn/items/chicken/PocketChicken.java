package net.guizhanss.gcereborn.items.chicken;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun5.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun5.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun5.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun5.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun5.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun5.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun5.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun5.utils.compatibility.PdcCompat;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.adapters.AnimalsAdapter;
import net.guizhanss.gcereborn.core.genetics.DNA;
import net.guizhanss.gcereborn.utils.ChickenUtils;
import net.guizhanss.gcereborn.utils.CompatUtils;
import net.guizhanss.gcereborn.utils.Keys;

public class PocketChicken extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable, DistinctiveItem {

    public static final AnimalsAdapter<Chicken> ADAPTER = new AnimalsAdapter<>(Chicken.class);

    public PocketChicken(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    @Nonnull
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Optional<Block> block = e.getClickedBlock();
            if (!block.isPresent()) {
                return;
            }

            Block b = block.get();
            Location location = b.getRelative(e.getClickedFace()).getLocation();
            Chicken entity = b.getWorld().spawn(CompatUtils.centerLocation(location), Chicken.class);

            ItemMeta meta = e.getItem().getItemMeta();
            JsonObject json = ChickenUtils.getAdapterData(meta);
            ADAPTER.apply(entity, json);
            DNA dna;
            if (ChickenUtils.hasDnaState(meta)) {
                dna = new DNA(ChickenUtils.getDnaState(meta));
            } else {
                dna = new DNA();
            }

            String dss = dna.getStateString();
            PdcCompat.setString(entity, Keys.CHICKEN_DNA, dss);

            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                ItemUtils.consumeItem(e.getItem(), false);
            }

            if (GeneticChickengineering.getConfigService().isDisplayResources() && dna.isKnown()) {
                String name = ChatColor.WHITE + "(" + ChickenTypes.getDisplayName(dna.getTyping()) + ")";
                if (json != null && !json.get("_customName").isJsonNull()) {
                    name = json.get("_customName").getAsString() + " " + name;
                }
                entity.setCustomName(name);
                entity.setCustomNameVisible(true);
            }
        };
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canStack(ItemMeta meta1, ItemMeta meta2) {
        // getPersistentDataContainer() is 1.14+, and PdcCompat.containersEqual() would resolve both
        // sides to "no container" (i.e. always equal) on legacy servers - which would let two chickens
        // with different DNA/health silently merge. Compare the two keys we actually care about instead,
        // through PdcCompat so this works identically on every version.
        String dna1 = PdcCompat.getString(meta1, Keys.POCKET_CHICKEN_DNA);
        String dna2 = PdcCompat.getString(meta2, Keys.POCKET_CHICKEN_DNA);
        String adapter1 = PdcCompat.getString(meta1, Keys.POCKET_CHICKEN_ADAPTER);
        String adapter2 = PdcCompat.getString(meta2, Keys.POCKET_CHICKEN_ADAPTER);
        return java.util.Objects.equals(dna1, dna2) && java.util.Objects.equals(adapter1, adapter2);
    }
}
