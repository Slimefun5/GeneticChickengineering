package net.guizhanss.gcereborn.items.chicken;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;

import net.guizhanss.gcereborn.GeneticChickengineering;

import lombok.Getter;

@Getter
public class ChickenProduct {

    private final String name;
    private final ItemStack product;

    @ParametersAreNonnullByDefault
    public ChickenProduct(String name, ItemStack product) {
        this.name = name.toUpperCase(Locale.ROOT);
        this.product = product;
    }

    @ParametersAreNonnullByDefault
    public ChickenProduct(ItemStack product) {
        this.name = product.getType().name().toUpperCase(Locale.ROOT);
        this.product = product;
    }

    /**
     * The fork's {@link SlimefunItemStack} no longer extends {@link ItemStack} (it wraps one via
     * {@link SlimefunItemStack#item()}), so it needs its own overload rather than the
     * {@code instanceof} check the upstream {@link #ChickenProduct(ItemStack)} constructor used.
     */
    @ParametersAreNonnullByDefault
    public ChickenProduct(SlimefunItemStack product) {
        this.name = product.getItemId();
        this.product = product.item();
    }

    @Nonnull
    public String getProductName() {
        return GeneticChickengineering.getLocalization().getString("products." + name);
    }
}
