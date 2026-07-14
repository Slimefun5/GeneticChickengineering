package net.guizhanss.gcereborn.utils;

import javax.annotation.Nonnull;

import org.bukkit.Location;

import io.github.thebusybiscuit.slimefun5.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun5.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XSound;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.particles.XParticle;

/**
 * Version-safety helpers so GeneticChickengineering loads + enables on legacy servers (1.8-1.13)
 * without a {@code NoSuchFieldError}/{@code NoClassDefFoundError} from post-1.8 constants and APIs.
 *
 * <p>Sounds are resolved <em>by name</em> through the fork's relocated XSeries wrapper, so a
 * modern-only value simply skips instead of referencing a constant that doesn't exist. Particle
 * spawning is gated behind a runtime version check and isolated in a dedicated method so the JVM
 * never resolves the 1.9+ {@code org.bukkit.Particle} class on 1.8.
 */
public final class CompatUtils {

    private CompatUtils() {}

    /** {@code World#spawnParticle} + {@code org.bukkit.Particle} are 1.9+. */
    public static boolean particlesSupported() {
        return Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_9);
    }

    /** {@code org.bukkit.attribute.*} (see {@link net.guizhanss.gcereborn.utils.AttributeCompat}) is 1.9+. */
    public static boolean attributesSupported() {
        return Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_9);
    }

    /**
     * {@code Location#toCenterLocation()} postdates the fork's Java-8-compatible spigot-api compile
     * baseline (1.16.5); this is a plain arithmetic equivalent (add 0.5 to every axis).
     */
    @Nonnull
    public static Location centerLocation(@Nonnull Location location) {
        return location.clone().add(0.5, 0.5, 0.5);
    }

    public static void playSound(@Nonnull Location location, @Nonnull String name, float volume, float pitch) {
        XSound.matchXSound(name).ifPresent(sound -> {
            if (sound.isSupported()) {
                sound.play(location, volume, pitch);
            }
        });
    }

    public static void spawnParticle(@Nonnull Location location, @Nonnull String name, int count, double offsetX, double offsetY, double offsetZ) {
        if (particlesSupported()) {
            doSpawnParticle(location, name, count, offsetX, offsetY, offsetZ);
        }
    }

    public static void spawnParticle(@Nonnull Location location, @Nonnull String name, int count) {
        spawnParticle(location, name, count, 0, 0, 0);
    }

    /**
     * References {@code org.bukkit.Particle} (1.9+) via {@link XParticle#getParticle(String)}. MUST
     * only be invoked behind {@link #particlesSupported()} so the JVM never resolves
     * {@code org.bukkit.Particle} on 1.8.
     */
    private static void doSpawnParticle(Location location, String name, int count, double offsetX, double offsetY, double offsetZ) {
        if (location.getWorld() == null) {
            return;
        }
        try {
            org.bukkit.Particle particle = XParticle.getParticle(name);
            if (particle != null) {
                location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
            }
        } catch (Throwable ignored) {
            // Particle absent on this version - skip silently rather than crash.
        }
    }
}
