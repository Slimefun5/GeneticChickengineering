package net.guizhanss.gcereborn.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;

/**
 * {@code org.bukkit.attribute.*} (used by the upstream {@code MobAdapter} to snapshot/restore a
 * captured mob's attributes) is a 1.9+ API absent from 1.8.8. Isolating every reference in its own
 * class means the JVM never has to resolve {@code Attribute}/{@code AttributeInstance}/
 * {@code AttributeModifier} unless this class is actually loaded, which {@link CompatUtils} only
 * does behind an {@code attributesSupported()} version check - the same isolation pattern
 * {@code CompatUtils#spawnParticle} uses for {@code org.bukkit.Particle} (1.9+).
 */
public final class AttributeCompat {

    private AttributeCompat() {}

    @Nonnull
    public static JsonObject collectAttributes(@Nonnull LivingEntity entity) {
        JsonObject attributes = new JsonObject();

        for (Attribute attribute : Attribute.values()) {
            AttributeInstance instance = entity.getAttribute(attribute);

            if (instance != null) {
                JsonObject obj = new JsonObject();
                obj.addProperty("base", instance.getBaseValue());

                JsonArray modifiers = new JsonArray();
                for (AttributeModifier modifier : instance.getModifiers()) {
                    JsonObject mod = new JsonObject();
                    mod.addProperty("uuid", modifier.getUniqueId().toString());
                    mod.addProperty("name", modifier.getName());
                    mod.addProperty("operation", modifier.getOperation().ordinal());
                    mod.addProperty("amount", modifier.getAmount());
                    modifiers.add(mod);
                }

                obj.add("modifiers", modifiers);
                attributes.add(attribute.toString(), obj);
            }
        }

        return attributes;
    }

    public static void applyAttributes(@Nonnull LivingEntity entity, @Nonnull JsonObject attributes) {
        for (Map.Entry<String, JsonElement> entry : attributes.entrySet()) {
            AttributeInstance instance = entity.getAttribute(Attribute.valueOf(entry.getKey()));

            if (instance != null) {
                for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
                    instance.removeModifier(modifier);
                }

                JsonObject attribute = entry.getValue().getAsJsonObject();
                instance.setBaseValue(attribute.get("base").getAsDouble());

                JsonArray modifiers = attribute.getAsJsonArray("modifiers");
                for (JsonElement modifier : modifiers) {
                    JsonObject obj = modifier.getAsJsonObject();

                    String uuid = obj.get("uuid").getAsString();
                    String name = obj.get("name").getAsString();
                    double amount = obj.get("amount").getAsDouble();
                    int operation = obj.get("operation").getAsInt();

                    AttributeModifier mod = new AttributeModifier(UUID.fromString(uuid), name, amount, AttributeModifier.Operation.values()[operation]);
                    instance.addModifier(mod);
                }
            }
        }
    }
}
