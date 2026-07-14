package net.guizhanss.gcereborn.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun5.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun5.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;
import io.github.thebusybiscuit.slimefun5.utils.compatibility.PdcCompat;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.genetics.DNA;
import net.guizhanss.gcereborn.core.services.LocalizationService;
import net.guizhanss.gcereborn.items.GCEItems;
import net.guizhanss.gcereborn.items.chicken.ChickenTypes;
import net.guizhanss.gcereborn.items.chicken.PocketChicken;
import net.guizhanss.gcereborn.setup.Groups;
import net.guizhanss.gcereborn.setup.RecipeTypes;

import lombok.experimental.UtilityClass;

/**
 * Utility class for {@link PocketChicken}.
 */
@UtilityClass
public final class ChickenUtils {

    /**
     * Determine whether an {@link ItemStack} is a {@link PocketChicken}.
     *
     * @param item The {@link ItemStack} to check.
     * @return Whether the {@link ItemStack} is a {@link PocketChicken}.
     */
    public boolean isPocketChicken(@Nullable ItemStack item) {
        return item != null && !item.getType().isAir() && item.hasItemMeta() && hasDnaState(item.getItemMeta());
    }

    /**
     * Get a json object representing a chicken.
     *
     * @return A json object representing a baby chicken.
     */
    @Nonnull
    public static JsonObject getChickenJson(boolean isBaby) {
        JsonObject json = new JsonObject();
        json.addProperty("_type", "CHICKEN");
        json.addProperty("_health", 4.0);
        json.addProperty("_absorption", 0.0);
        json.addProperty("_removeWhenFarAway", false);
        json.addProperty("_customName", (String) null);
        json.addProperty("_customNameVisible", false);
        json.addProperty("_ai", true);
        json.addProperty("_silent", false);
        json.addProperty("_glowing", false);
        json.addProperty("_invulnerable", false);
        json.addProperty("_collidable", true);
        json.addProperty("_gravity", true);
        json.addProperty("_fireTicks", 0);
        json.addProperty("baby", isBaby);
        json.addProperty("_age", isBaby ? -24000 : 6000);
        json.addProperty("_ageLock", false);
        json.addProperty("_breedable", false);
        json.addProperty("_loveModeTicks", 0);
        json.add("_attributes", new JsonObject());
        json.add("_effects", new JsonObject());
        json.add("_scoreboardTags", new JsonArray());
        return json;
    }

    /**
     * Captures a {@link Chicken} and returns a pocket chicken item.
     *
     * @param chicken The {@link Chicken} to capture.
     * @return The pocket chicken item.
     */
    @Nonnull
    public static ItemStack capture(@Nonnull Chicken chicken) {
        GeneticChickengineering.getIntegrationService().captureChicken(chicken);
        JsonObject json = PocketChicken.ADAPTER.saveData(chicken);
        ItemStack item = GCEItems.POCKET_CHICKEN.clone().item();

        DNA dna;
        String dnaStr = PdcCompat.getString(chicken, Keys.CHICKEN_DNA);
        if (dnaStr != null) {
            GeneticChickengineering.debug("captured chicken has data in pdc: {0}", dnaStr);
            dna = new DNA(dnaStr);
        } else {
            GeneticChickengineering.debug("captured chicken has no DNA information");
            dna = new DNA();
        }

        if (GeneticChickengineering.getConfigService().isDisplayResources() && json.get("_customNameVisible").getAsBoolean() && dna.isKnown()) {
            String name;
            if (!json.get("_customName").isJsonNull()) {
                name = json.get("_customName").getAsString();
            } else {
                name = "";
            }
            String replace = "(" + ChickenTypes.getDisplayName(dna.getTyping()) + ")";
            name = name.replace(replace, "");
            if (name.isEmpty()) {
                json.addProperty("_customName", (String) null);
                json.addProperty("_customNameVisible", false);
            } else {
                json.addProperty("_customName", name);
            }
        }

        setPocketChicken(item, json, dna);
        return item;
    }

    /**
     * Try to breed two chicken.
     *
     * @param chick1 The first chicken.
     * @param chick2 The second chicken.
     * @return The resulting baby chicken, or null if breeding failed.
     */
    @Nullable
    @ParametersAreNonnullByDefault
    public static ItemStack breed(ItemStack chick1, ItemStack chick2) {
        Preconditions.checkArgument(chick1 != null, "chick1 cannot be null");
        Preconditions.checkArgument(chick2 != null, "chick2 cannot be null");

        ItemMeta c1m = chick1.getItemMeta();
        ItemMeta c2m = chick2.getItemMeta();
        if (hasDnaState(c1m) && hasDnaState(c2m)) {
            DNA c1d = new DNA(getDnaState(c1m));
            DNA c2d = new DNA(getDnaState(c2m));
            return fromDNA(new DNA(c1d.split(), c2d.split()), true);
        }
        return null;
    }

    /**
     * Creates a display item for the given product in the dictionary.
     *
     * @param typing The type of chicken.
     */
    public static void createProductDisplay(int typing) {
        ItemStack fake = GCEItems.POCKET_CHICKEN.clone().item();
        DNA dna = new DNA(typing);
        setPocketChicken(fake, null, dna);

        // The bare (id, ItemStack) ctor left these dictionary icons nameless, so in-game they rendered
        // as their raw id. Give each icon the localized product name suffixed with " Chicken" (matching
        // the "&e{0} &eChicken" DNA-type lore), falling back to the raw product name if unlocalized.
        String productName = ChickenTypes.getDisplayName(typing);
        if (productName.isEmpty()) {
            productName = ChickenTypes.getName(typing);
        }
        String displayName = "&f" + productName + " Chicken";

        // The id is keyed on the typing (0-63), NOT the product material name: on legacy servers
        // (1.8-1.15) many modern products fall back to the same substitute material via MaterialCompat
        // (e.g. STONE), which made material-name ids collide ("GCE_STONE_CHICKEN_ICON" twice) and throw
        // an IdConflictException. The typing is the canonical, version-stable identity of a chicken.
        SlimefunItemStack displayItem = new SlimefunItemStack("GCE_CHICKEN_ICON_" + typing, ChickenTypes.getProduct(typing), displayName);
        // Since these will be "Pocket Chickens", they will spawn chickens when cheated into a player's inventory
        // We set the DNA on the icon so that it will spawn a chicken of the correct type
        ItemMeta meta = displayItem.getItemMeta();
        setDnaState(meta, dna.getState());
        displayItem.setItemMeta(meta);

        // Register the display
        // @formatter:off
        new PocketChicken(
            Groups.DICTIONARY,
            displayItem,
            RecipeTypes.FROM_CHICKEN,
            new ItemStack[] {
                null, null, null,
                null, fake, null,
                null, null, null
            }
        ).register(GeneticChickengineering.getInstance());
        // @formatter:on
    }

    /**
     * Create a fresh new chicken based on the DNA.
     *
     * @param dna    The DNA to use.
     * @param isBaby Whether the chicken is a baby.
     * @return The new chicken item.
     */
    @Nonnull
    public static ItemStack fromDNA(@Nonnull DNA dna, boolean isBaby) {
        JsonObject json = getChickenJson(isBaby);

        ItemStack item = GCEItems.POCKET_CHICKEN.clone().item();
        setPocketChicken(item, json, dna);
        return item;
    }

    @Nonnull
    public static DNA getDNA(@Nonnull ItemStack chicken) {
        ItemMeta meta = chicken.getItemMeta();
        return new DNA(getDnaState(meta));
    }

    /**
     * Returns a number which reflects the number of homozygous dominant alleles in a chicken.
     * This is used to give a boosted rate to resource production from chickens which are "pure".
     *
     * @param chicken The chicken {@link ItemStack}.
     * @return The DNA strength.
     */
    public static int getDNAStrength(@Nonnull ItemStack chicken) {
        DNA dna = getDNA(chicken);
        int[] state = dna.getState();
        int str = 6 - dna.getTier();
        for (int i = 0; i < 6; i++) {
            if (state[i] == 1) {
                str--;
            }
        }
        return str;
    }

    @Nonnull
    private static List<String> getLore(@Nullable JsonObject json, @Nonnull DNA dna) {
        List<String> lore = new LinkedList<>();
        LocalizationService localization = GeneticChickengineering.getLocalization();
        if (json != null) {
            lore = PocketChicken.ADAPTER.getLore(json);
            if (GeneticChickengineering.getConfigService().isPainEnabled()) {
                double health = json.get("_health").getAsDouble();
                String status;
                if (health > 2.0) {
                    status = localization.getString("lores.chicken.status.healthy");
                } else if (health <= 0.50) {
                    status = localization.getString("lores.chicken.status.exhausted");
                } else {
                    status = localization.getString("lores.chicken.status.fatigued");
                }
                lore.add(localization.getString("lores.chicken.status.line", status));
            }
        }
        if (dna.isKnown()) {
            lore.add(localization.getString("lores.chicken.dna", dna));
            lore.add(localization.getString("lores.chicken.type", ChickenTypes.getDisplayName(dna.getTyping())));
        }
        return lore;
    }

    public static void setPocketChicken(@Nonnull ItemStack item, @Nullable JsonObject json, @Nonnull DNA dna) {
        ItemMeta meta = item.getItemMeta();
        setDnaState(meta, dna.getState());
        if (json != null) {
            setAdapterData(meta, json);
        }
        meta.setLore(getLore(json, dna));

        item.setItemMeta(meta);
    }

    public double getHealth(@Nullable ItemStack chicken) {
        if (chicken == null || chicken.getType().isAir()) {
            return 0d;
        }
        ItemMeta meta = chicken.getItemMeta();
        JsonObject json = getAdapterData(meta);
        if (json != null) {
            return json.get("_health").getAsDouble();
        }
        return 0d;
    }

    public boolean survivesPain(@Nullable ItemStack chicken) {
        return getHealth(chicken) > 0.25;
    }

    public boolean harm(@Nullable ItemStack chicken) {
        return harm(chicken, 0.25);
    }

    public boolean harm(@Nullable ItemStack chicken, double damage) {
        if (chicken == null || chicken.getType().isAir()) {
            return false;
        }
        ItemMeta meta = chicken.getItemMeta();
        JsonObject json = getAdapterData(meta);
        if (json != null) {
            double oldHealth = json.get("_health").getAsDouble();
            double newHealth = Math.max(0d, Math.min(oldHealth - damage, 4d));
            json.addProperty("_health", newHealth);
            setPocketChicken(chicken, json, getDNA(chicken));
            return true;
        }
        return false;
    }

    public boolean heal(@Nullable ItemStack chicken, double amount) {
        if (amount > 0) {
            amount *= -1;
        }
        return harm(chicken, amount);
    }

    public void possiblyHarm(@Nullable ItemStack chicken) {
        if (ThreadLocalRandom.current().nextInt(100) < GeneticChickengineering.getConfigService().getPainChance()) {
            harm(chicken);
        }
    }

    @Nonnull
    public ItemStack getResource(@Nonnull ItemStack chicken) {
        DNA dna = getDNA(chicken);
        return ChickenTypes.getProduct(dna.getTyping());
    }

    /**
     * Returns the number of homozygous recessive genes in the chicken
     * which represents the difficulty of obtaining this chicken
     *
     * @param chicken The chicken {@link ItemStack}.
     * @return The DNA tier.
     */
    public int getResourceTier(@Nonnull ItemStack chicken) {
        DNA dna = getDNA(chicken);
        return dna.getTier();
    }

    public boolean isFood(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || item.hasItemMeta()) {
            return false;
        }
        Material type = item.getType();
        if (type == MaterialCompat.safe(XMaterial.WHEAT_SEEDS) || type == MaterialCompat.safe(XMaterial.BEETROOT_SEEDS)
            || type == MaterialCompat.safe(XMaterial.MELON_SEEDS) || type == MaterialCompat.safe(XMaterial.PUMPKIN_SEEDS)) {
            return true;
        }

        // Fine-grained (1.19.4) version checks aren't available on the fork's MinecraftVersion enum
        // (only whole-minor-version constants); MaterialCompat.safe() degrades gracefully regardless
        // (see its legacy-substitute map), so a slightly conservative gate here is harmless.
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_19) && type == MaterialCompat.safe(XMaterial.TORCHFLOWER_SEEDS)) {
            return true;
        }

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20) && type == MaterialCompat.safe(XMaterial.PITCHER_POD)) {
            return true;
        }

        return false;
    }

    /**
     * Determines whether the chicken is an adult.
     *
     * @param chicken The chicken {@link ItemStack}.
     * @return Whether the chicken is an adult.
     */
    public boolean isAdult(@Nonnull ItemStack chicken) {
        JsonObject json = getAdapterData(chicken.getItemMeta());
        if (json != null) {
            return !json.get("baby").getAsBoolean();
        }
        return false;
    }

    /**
     * Determines whether the DNA of chicken is known.
     *
     * @param chicken The chicken {@link ItemStack}.
     * @return Whether the DNA is known.
     */
    public boolean isLearned(@Nonnull ItemStack chicken) {
        DNA dna = getDNA(chicken);
        return dna.isKnown();
    }

    /**
     * Learn the DNA of a chicken. This returns a new {@link ItemStack} with known DNA.
     *
     * @param chicken The chicken {@link ItemStack}.
     * @return The new chicken {@link ItemStack}.
     */
    @Nonnull
    public ItemStack learnDNA(@Nonnull ItemStack chicken) {
        ItemStack item = chicken.clone();
        ItemMeta meta = item.getItemMeta();

        if (hasDnaState(meta)) {
            DNA dna = new DNA(getDnaState(meta));
            dna.learn();
            JsonObject json = getAdapterData(meta);
            setPocketChicken(item, json, dna);
        }

        return item;
    }

    // --- PDC helpers ---
    //
    // The upstream Reborn build persisted the chicken DNA (int[]) and the full mob snapshot
    // (a JsonObject, via a custom PersistentDataType adapter) directly through dough's
    // PersistentDataAPI. Neither is 1.8-safe: PersistentDataType/PersistentDataContainer are 1.14+
    // APIs. Both are now string-encoded and stored through PdcCompat, whose fallback path (real item
    // NBT for ItemMeta, a YAML store keyed by entity UUID for live entities) keeps this working on
    // legacy servers, see io.github.thebusybiscuit.slimefun5.utils.compatibility.PdcCompat.

    public static boolean hasDnaState(@Nonnull Object holder) {
        return PdcCompat.has(holder, Keys.POCKET_CHICKEN_DNA, "STRING");
    }

    @Nonnull
    public static int[] getDnaState(@Nonnull Object holder) {
        return decodeIntArray(PdcCompat.getString(holder, Keys.POCKET_CHICKEN_DNA));
    }

    public static void setDnaState(@Nonnull Object holder, @Nonnull int[] state) {
        PdcCompat.setString(holder, Keys.POCKET_CHICKEN_DNA, encodeIntArray(state));
    }

    @Nullable
    public static JsonObject getAdapterData(@Nonnull Object holder) {
        String raw = PdcCompat.getString(holder, Keys.POCKET_CHICKEN_ADAPTER);
        return raw != null ? PocketChicken.ADAPTER.fromPrimitive(raw) : null;
    }

    public static void setAdapterData(@Nonnull Object holder, @Nonnull JsonObject json) {
        PdcCompat.setString(holder, Keys.POCKET_CHICKEN_ADAPTER, PocketChicken.ADAPTER.toPrimitive(json));
    }

    @Nonnull
    private static String encodeIntArray(@Nonnull int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    @Nonnull
    private static int[] decodeIntArray(@Nullable String raw) {
        if (raw == null || raw.isEmpty()) {
            return new int[7];
        }
        String[] parts = raw.split(",");
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Integer.parseInt(parts[i].trim());
        }
        return values;
    }
}
