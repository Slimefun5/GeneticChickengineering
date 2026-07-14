package net.guizhanss.gcereborn.utils;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Material;

import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;

/**
 * Resolves {@link XMaterial} constants to a {@link Material} that exists on the running server.
 * Keeps GeneticChickengineering loadable on legacy versions (e.g. 1.8) where modern constants
 * (nether-update blocks, 1.13 renames, etc.) are absent.
 */
public final class MaterialCompat {

    private MaterialCompat() {}

    @Nonnull
    public static Material safe(@Nonnull XMaterial material) {
        Material resolved = material.parseMaterial();
        if (resolved == null) {
            resolved = substitute(material);
        }
        return resolved != null ? resolved : Material.STONE;
    }

    // Sensible legacy substitutes for materials that don't exist on older servers (e.g. 1.8-1.15).
    private static final Map<XMaterial, XMaterial> LEGACY_SUBSTITUTES = buildLegacySubstitutes();

    private static Map<XMaterial, XMaterial> buildLegacySubstitutes() {
        Map<XMaterial, XMaterial> m = new EnumMap<>(XMaterial.class);

        // 1.13 "The Flattening" renames / new blocks
        m.put(XMaterial.DIORITE, XMaterial.STONE);
        m.put(XMaterial.ANDESITE, XMaterial.STONE);
        m.put(XMaterial.GRANITE, XMaterial.STONE);
        m.put(XMaterial.KELP, XMaterial.SEAGRASS);
        m.put(XMaterial.PRISMARINE_CRYSTALS, XMaterial.PRISMARINE_SHARD);
        m.put(XMaterial.PRISMARINE_SHARD, XMaterial.QUARTZ);
        m.put(XMaterial.PHANTOM_MEMBRANE, XMaterial.LEATHER);
        m.put(XMaterial.WHEAT_SEEDS, XMaterial.WHEAT);
        m.put(XMaterial.BEETROOT_SEEDS, XMaterial.BEETROOT);
        m.put(XMaterial.MELON_SEEDS, XMaterial.MELON);
        m.put(XMaterial.PUMPKIN_SEEDS, XMaterial.PUMPKIN);
        m.put(XMaterial.TURTLE_SPAWN_EGG, XMaterial.CHICKEN_SPAWN_EGG);
        m.put(XMaterial.STRIDER_SPAWN_EGG, XMaterial.CHICKEN_SPAWN_EGG);
        m.put(XMaterial.BLAST_FURNACE, XMaterial.FURNACE);
        m.put(XMaterial.SMOKER, XMaterial.FURNACE);
        m.put(XMaterial.OBSERVER, XMaterial.PISTON);
        m.put(XMaterial.PINK_SHULKER_BOX, XMaterial.CHEST);
        m.put(XMaterial.GREEN_SHULKER_BOX, XMaterial.CHEST);
        m.put(XMaterial.BLACK_STAINED_GLASS_PANE, XMaterial.GLASS_PANE);

        // 1.16 Nether Update
        m.put(XMaterial.BLACKSTONE, XMaterial.COBBLESTONE);
        m.put(XMaterial.CRYING_OBSIDIAN, XMaterial.OBSIDIAN);
        m.put(XMaterial.SOUL_SOIL, XMaterial.SOUL_SAND);
        m.put(XMaterial.SHROOMLIGHT, XMaterial.GLOWSTONE);
        m.put(XMaterial.BASALT, XMaterial.STONE);
        m.put(XMaterial.CHAIN, XMaterial.IRON_BARS);
        m.put(XMaterial.NETHERITE_INGOT, XMaterial.IRON_INGOT);

        // 1.14+ villages
        m.put(XMaterial.BIRCH_PLANKS, XMaterial.OAK_PLANKS);
        m.put(XMaterial.RED_BED, XMaterial.RED_WOOL);
        m.put(XMaterial.WHITE_BED, XMaterial.WHITE_WOOL);
        m.put(XMaterial.PINK_TERRACOTTA, XMaterial.PINK_WOOL);
        m.put(XMaterial.HAY_BLOCK, XMaterial.HAY_BLOCK);

        // Torchflower / Pitcher (1.19.4+/1.20+)
        m.put(XMaterial.TORCHFLOWER_SEEDS, XMaterial.WHEAT_SEEDS);
        m.put(XMaterial.PITCHER_POD, XMaterial.WHEAT_SEEDS);

        return m;
    }

    private static Material substitute(XMaterial xMaterial) {
        XMaterial sub = LEGACY_SUBSTITUTES.get(xMaterial);
        if (sub == null) {
            return null;
        }
        Material resolved = sub.parseMaterial();
        if (resolved != null) {
            return resolved;
        }
        // The substitute itself might need a substitute (chained fallback), one level deep.
        XMaterial subSub = LEGACY_SUBSTITUTES.get(sub);
        return subSub != null ? subSub.parseMaterial() : null;
    }
}
