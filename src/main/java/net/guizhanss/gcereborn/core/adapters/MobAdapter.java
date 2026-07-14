package net.guizhanss.gcereborn.core.adapters;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.entity.LivingEntity;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.core.services.LocalizationService;
import net.guizhanss.gcereborn.utils.AttributeCompat;
import net.guizhanss.gcereborn.utils.CompatUtils;
import net.guizhanss.gcereborn.utils.EntityCompat;

/**
 * This class is a wholesale copy of TheBusyBiscuit's MobCapturer.
 * <p>
 * This is a simple Adapter that allows conversion between a {@link LivingEntity} and
 * a {@link JsonObject}.
 * <p>
 * Java-8 port note: this no longer {@code implements org.bukkit.persistence.PersistentDataType}
 * (a 1.14+-only interface - implementing it would throw {@code NoClassDefFoundError} loading this
 * class on 1.8-1.13). {@link #toPrimitive(JsonObject)}/{@link #fromPrimitive(String)} are now plain
 * conversion methods; storage goes through
 * {@link io.github.thebusybiscuit.slimefun5.utils.compatibility.PdcCompat}'s String primitive instead
 * of a real {@code PersistentDataContainer} entry (see {@code ChickenUtils}/{@code PocketChicken}).
 * Attribute snapshotting (1.9+) is isolated in {@link AttributeCompat}, gated by
 * {@link CompatUtils#attributesSupported()}, for the same reason.
 */
public interface MobAdapter<T extends LivingEntity> {

    Class<T> getEntityClass();

    default List<String> getLore(JsonObject json) {
        List<String> lore = new LinkedList<>();
        LocalizationService localization = GeneticChickengineering.getLocalization();

        lore.add("");
        lore.add(localization.getString("lores.chicken.health", json.get("_health").getAsDouble()));

        if (!json.get("_customName").isJsonNull()) {
            lore.add(localization.getString("lores.chicken.name", json.get("_customName").getAsString()));
        }

        int fireTicks = json.get("_fireTicks").getAsInt();
        if (fireTicks > 0) {
            lore.add(localization.getString("lores.chicken.on-fire"));
        }

        return lore;
    }

    default String toPrimitive(JsonObject json) {
        return json.toString();
    }

    default JsonObject fromPrimitive(String primitive) {
        return new JsonParser().parse(primitive).getAsJsonObject();
    }

    default void apply(T entity, JsonObject json) {
        // We need to apply Attributes before the health.
        if (CompatUtils.attributesSupported()) {
            AttributeCompat.applyAttributes(entity, json.getAsJsonObject("_attributes"));
        }

        entity.setHealth(json.get("_health").getAsDouble());
        entity.setRemoveWhenFarAway(json.get("_removeWhenFarAway").getAsBoolean());

        if (!json.get("_customName").isJsonNull()) {
            entity.setCustomName(json.get("_customName").getAsString());
        }

        entity.setCustomNameVisible(json.get("_customNameVisible").getAsBoolean());
        entity.setFireTicks(json.get("_fireTicks").getAsInt());

        // Post-1.8 flags/effects/tags - skipped gracefully on legacy servers (see EntityCompat).
        EntityCompat.applyModernState(entity, json);
        EntityCompat.applyPotionEffects(entity, json.getAsJsonObject("_effects"));
        EntityCompat.applyScoreboardTags(entity, json.getAsJsonArray("_scoreboardTags"));
    }

    default JsonObject saveData(T entity) {
        JsonObject json = new JsonObject();

        json.addProperty("_type", entity.getType().toString());
        json.addProperty("_health", entity.getHealth());
        json.addProperty("_removeWhenFarAway", entity.getRemoveWhenFarAway());
        json.addProperty("_customName", entity.getCustomName());
        json.addProperty("_customNameVisible", entity.isCustomNameVisible());
        json.addProperty("_fireTicks", entity.getFireTicks());

        // Post-1.8 flags default gracefully on legacy servers (see EntityCompat).
        EntityCompat.saveModernState(entity, json);

        JsonObject attributes = CompatUtils.attributesSupported() ? AttributeCompat.collectAttributes(entity) : new JsonObject();
        json.add("_attributes", attributes);

        JsonObject effects = new JsonObject();
        EntityCompat.savePotionEffects(entity, effects);
        json.add("_effects", effects);

        JsonArray tags = new JsonArray();
        EntityCompat.saveScoreboardTags(entity, tags);
        json.add("_scoreboardTags", tags);

        return json;
    }

}
