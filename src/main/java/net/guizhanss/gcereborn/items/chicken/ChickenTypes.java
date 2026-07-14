package net.guizhanss.gcereborn.items.chicken;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.items.GCEItems;
import net.guizhanss.gcereborn.utils.ChickenUtils;
import net.guizhanss.gcereborn.utils.MaterialCompat;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ChickenTypes {

    private static final Map<Integer, ChickenProduct> TYPES = new LinkedHashMap<>();

    static {
        TYPES.put(63, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.FEATHER))));
        TYPES.put(31, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.BONE))));
        TYPES.put(47, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.COBBLESTONE))));
        TYPES.put(55, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.DIRT))));
        TYPES.put(59, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.FLINT))));
        TYPES.put(61, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SAND))));
        TYPES.put(62, new ChickenProduct("WATER", GCEItems.WATER_EGG.item()));
        TYPES.put(15, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.COAL))));
        TYPES.put(23, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.STRING))));
        TYPES.put(27, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.LEATHER))));
        TYPES.put(29, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SUGAR))));
        TYPES.put(30, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SPONGE))));
        TYPES.put(39, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.DIORITE))));
        TYPES.put(43, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.ANDESITE))));
        TYPES.put(45, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.GRAVEL))));
        TYPES.put(46, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.ICE))));
        TYPES.put(51, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.GRANITE))));
        TYPES.put(53, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.CLAY))));
        TYPES.put(54, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.OAK_LOG))));
        TYPES.put(57, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.GUNPOWDER))));
        TYPES.put(58, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.KELP))));
        TYPES.put(60, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SLIME_BALL))));
        TYPES.put(7, new ChickenProduct("GOLD", new ItemStack(MaterialCompat.safe(XMaterial.GOLD_INGOT))));
        TYPES.put(11, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.NETHERRACK))));
        TYPES.put(13, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.GLASS))));
        TYPES.put(14, new ChickenProduct("LAPIS", new ItemStack(MaterialCompat.safe(XMaterial.LAPIS_LAZULI))));
        TYPES.put(19, new ChickenProduct("IRON", new ItemStack(MaterialCompat.safe(XMaterial.IRON_INGOT))));
        TYPES.put(21, new ChickenProduct(SlimefunItems.IRON_DUST));
        TYPES.put(22, new ChickenProduct(SlimefunItems.GOLD_DUST));
        TYPES.put(25, new ChickenProduct(SlimefunItems.SILVER_DUST));
        TYPES.put(26, new ChickenProduct(SlimefunItems.ZINC_DUST));
        TYPES.put(28, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.CAKE))));
        TYPES.put(35, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.OBSIDIAN))));
        TYPES.put(37, new ChickenProduct(SlimefunItems.COPPER_DUST));
        TYPES.put(38, new ChickenProduct(SlimefunItems.MAGNESIUM_DUST));
        TYPES.put(41, new ChickenProduct("LAVA", GCEItems.LAVA_EGG.item()));
        TYPES.put(42, new ChickenProduct(SlimefunItems.TIN_DUST));
        TYPES.put(44, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SNOWBALL))));
        TYPES.put(49, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.REDSTONE))));
        TYPES.put(50, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.CACTUS))));
        TYPES.put(52, new ChickenProduct(SlimefunItems.ALUMINUM_DUST));
        TYPES.put(56, new ChickenProduct(SlimefunItems.LEAD_DUST));
        TYPES.put(3, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.BLACKSTONE))));
        TYPES.put(5, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SOUL_SOIL))));
        TYPES.put(9, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.BLAZE_ROD))));
        TYPES.put(17, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.GHAST_TEAR))));
        TYPES.put(33, new ChickenProduct(SlimefunItems.SULFATE));
        TYPES.put(6, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SHROOMLIGHT))));
        TYPES.put(10, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.QUARTZ))));
        TYPES.put(18, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.BASALT))));
        TYPES.put(34, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.CRYING_OBSIDIAN))));
        TYPES.put(12, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.SOUL_SAND))));
        TYPES.put(20, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.ENDER_PEARL))));
        TYPES.put(36, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.NETHER_WART))));
        TYPES.put(24, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.PHANTOM_MEMBRANE))));
        TYPES.put(40, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.MAGMA_CREAM))));
        TYPES.put(48, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.GLOWSTONE_DUST))));
        TYPES.put(1, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.DIAMOND))));
        TYPES.put(2, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.END_STONE))));
        TYPES.put(4, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.PRISMARINE_CRYSTALS))));
        TYPES.put(8, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.PRISMARINE_SHARD))));
        TYPES.put(16, new ChickenProduct("EXPERIENCE", new ItemStack(MaterialCompat.safe(XMaterial.EXPERIENCE_BOTTLE))));
        TYPES.put(32, new ChickenProduct(new ItemStack(MaterialCompat.safe(XMaterial.EMERALD))));
        TYPES.put(0, new ChickenProduct("NETHERITE", new ItemStack(MaterialCompat.safe(XMaterial.NETHERITE_INGOT))));
    }

    @Nonnull
    public static ChickenProduct get(int typing) {
        return TYPES.get(typing);
    }

    @Nonnull
    public static String getName(int typing) {
        return TYPES.get(typing).getName();
    }

    @Nonnull
    public static String getDisplayName(int typing) {
        return TYPES.get(typing).getProductName();
    }

    @Nonnull
    public static ItemStack getProduct(int typing) {
        return TYPES.get(typing).getProduct();
    }

    public static void registerChickens() {
        for (int i = TYPES.size() - 1; i >= 0; i--) {
            ChickenUtils.createProductDisplay(i);
        }
        GeneticChickengineering.log(Level.INFO,
            GeneticChickengineering.getLocalization().getString("console.load.chickens", TYPES.size()));
    }

}
