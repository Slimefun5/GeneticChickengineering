package net.guizhanss.gcereborn.items;

import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;

import net.guizhanss.gcereborn.utils.MaterialCompat;

import lombok.experimental.UtilityClass;

/**
 * Static Slimefun items of this addon.
 * <p>
 * Names and lore are no longer baked in Java: every item is constructed name-less (id + material/head)
 * and its display name plus the Type/Description/Stats/Usage lore blocks are authored per language in
 * {@code languages/<lang>/items.yml}, resolved at runtime by the fork's
 * {@code ItemTranslationService} (wired via {@code registerTranslations} in the main class). This
 * replaces the deprecated machine/power lore-builder calls and the addon's own
 * {@code getLocalization().getItem(...)} name lookup.
 */
@UtilityClass
public final class GCEItems {

    public static final SlimefunItemStack POCKET_CHICKEN = new SlimefunItemStack(
        "POCKET_CHICKEN",
        "1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893"
    );
    public static final SlimefunItemStack CHICKEN_NET = new SlimefunItemStack(
        "CHICKEN_NET",
        MaterialCompat.safe(XMaterial.COBWEB)
    );
    public static final SlimefunItemStack WATER_EGG = new SlimefunItemStack(
        "WATER_EGG",
        MaterialCompat.safe(XMaterial.TURTLE_SPAWN_EGG)
    );
    public static final SlimefunItemStack LAVA_EGG = new SlimefunItemStack(
        "LAVA_EGG",
        MaterialCompat.safe(XMaterial.STRIDER_SPAWN_EGG)
    );
    public static final SlimefunItemStack GENETIC_SEQUENCER = new SlimefunItemStack(
        "GENETIC_SEQUENCER",
        MaterialCompat.safe(XMaterial.SMOKER)
    );
    public static final SlimefunItemStack EXCITATION_CHAMBER = new SlimefunItemStack(
        "EXCITATION_CHAMBER",
        MaterialCompat.safe(XMaterial.BLAST_FURNACE)
    );
    public static final SlimefunItemStack EXCITATION_CHAMBER_2 = new SlimefunItemStack(
        "EXCITATION_CHAMBER_2",
        MaterialCompat.safe(XMaterial.BLAST_FURNACE)
    );
    public static final SlimefunItemStack EXCITATION_CHAMBER_3 = new SlimefunItemStack(
        "EXCITATION_CHAMBER_3",
        MaterialCompat.safe(XMaterial.BLAST_FURNACE)
    );
    public static final SlimefunItemStack PRIVATE_COOP = new SlimefunItemStack(
        "PRIVATE_COOP",
        MaterialCompat.safe(XMaterial.BEEHIVE)
    );
    public static final SlimefunItemStack RESTORATION_CHAMBER = new SlimefunItemStack(
        "RESTORATION_CHAMBER",
        MaterialCompat.safe(XMaterial.PINK_SHULKER_BOX)
    );
    public static final SlimefunItemStack GROWTH_CHAMBER = new SlimefunItemStack(
        "GROWTH_CHAMBER",
        MaterialCompat.safe(XMaterial.GREEN_SHULKER_BOX)
    );
}
