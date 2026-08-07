package net.guizhanss.gcereborn.utils;

import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Method-level version safety for the mob snapshot ({@code MobAdapter}/{@code AnimalsAdapter}).
 *
 * <p>The upstream MobCapturer copy calls a raft of post-1.8 {@code Entity}/{@code LivingEntity}/
 * {@code Ageable} methods: {@code setGlowing}/{@code setInvulnerable}/{@code setCollidable}/
 * {@code setAI}/{@code addScoreboardTag} (1.9), {@code setSilent}/{@code setGravity} (1.10),
 * {@code getAbsorptionAmount} (1.15), {@code getLoveModeTicks} (1.16), and the 6-arg
 * {@link PotionEffect} constructor + {@link PotionEffect#hasIcon()} (1.13). Those classes all exist
 * on 1.8 (so referencing them never throws {@code NoClassDefFoundError}), but the methods do not, so
 * each call is wrapped here: a captured/released chicken simply drops the unsupported fidelity fields
 * on legacy servers instead of throwing {@code NoSuchMethodError}. Basic properties (health, custom
 * name, age, baby flag, fire ticks) go through the always-present API and are handled by the caller.
 */
public final class EntityCompat {

    private EntityCompat() {}

    /** Run a possibly-unsupported mutation; swallow {@code NoSuchMethodError} on legacy servers. */
    public static void run(@Nonnull Runnable action) {
        try {
            action.run();
        } catch (Throwable ignored) {
            // Method absent on this server version - skip that field.
        }
    }

    /** Read a possibly-unsupported property, falling back to {@code fallback} on legacy servers. */
    public static <R> R get(@Nonnull Supplier<R> supplier, R fallback) {
        try {
            return supplier.get();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    public static void applyModernState(@Nonnull LivingEntity entity, @Nonnull JsonObject json) {
        run(() -> entity.setAbsorptionAmount(json.get("_absorption").getAsDouble()));
        run(() -> entity.setAI(json.get("_ai").getAsBoolean()));
        run(() -> entity.setSilent(json.get("_silent").getAsBoolean()));
        run(() -> entity.setGlowing(json.get("_glowing").getAsBoolean()));
        run(() -> entity.setInvulnerable(json.get("_invulnerable").getAsBoolean()));
        run(() -> entity.setCollidable(json.get("_collidable").getAsBoolean()));
        run(() -> entity.setGravity(json.get("_gravity").getAsBoolean()));
    }

    public static void applyPotionEffects(@Nonnull LivingEntity entity, @Nonnull JsonObject effects) {
        for (Map.Entry<String, JsonElement> entry : effects.entrySet()) {
            PotionEffectType type = PotionEffectType.getByName(entry.getKey());
            if (type == null) {
                continue;
            }

            JsonObject obj = entry.getValue().getAsJsonObject();
            int duration = obj.get("duration").getAsInt();
            int amplifier = obj.get("amplifier").getAsInt();
            boolean ambient = obj.get("ambient").getAsBoolean();
            boolean particles = obj.get("particles").getAsBoolean();
            boolean icon = obj.get("icon").getAsBoolean();

            // The 6-arg (icon) ctor is 1.13+; fall back to the 4-arg on older servers.
            PotionEffect effect = get(
                () -> new PotionEffect(type, duration, amplifier, ambient, particles, icon),
                new PotionEffect(type, duration, amplifier, ambient));
            run(() -> entity.addPotionEffect(effect));
        }
    }

    public static void applyScoreboardTags(@Nonnull Entity entity, @Nonnull JsonArray tags) {
        for (JsonElement tag : tags) {
            run(() -> entity.addScoreboardTag(tag.getAsString()));
        }
    }

    public static void saveModernState(@Nonnull LivingEntity entity, @Nonnull JsonObject json) {
        json.addProperty("_absorption", get(entity::getAbsorptionAmount, 0.0));
        json.addProperty("_ai", get(entity::hasAI, true));
        json.addProperty("_silent", get(entity::isSilent, false));
        json.addProperty("_glowing", get(entity::isGlowing, false));
        json.addProperty("_invulnerable", get(entity::isInvulnerable, false));
        json.addProperty("_collidable", get(entity::isCollidable, true));
        json.addProperty("_gravity", get(entity::hasGravity, true));
    }

    public static void savePotionEffects(@Nonnull LivingEntity entity, @Nonnull JsonObject effects) {
        for (PotionEffect effect : entity.getActivePotionEffects()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("duration", effect.getDuration());
            obj.addProperty("amplifier", effect.getAmplifier());
            obj.addProperty("ambient", effect.isAmbient());
            obj.addProperty("particles", get(effect::hasParticles, true));
            obj.addProperty("icon", get(effect::hasIcon, false));
            effects.add(effect.getType().getName(), obj);
        }
    }

    public static void saveScoreboardTags(@Nonnull Entity entity, @Nonnull JsonArray tags) {
        for (String tag : get(entity::getScoreboardTags, java.util.Collections.<String>emptySet())) {
            tags.add(tag);
        }
    }
}
