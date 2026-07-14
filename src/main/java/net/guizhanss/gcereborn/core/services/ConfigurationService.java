package net.guizhanss.gcereborn.core.services;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun5.libraries.dough.config.Config;

import net.guizhanss.gcereborn.GeneticChickengineering;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Reimplemented against the fork's dough {@link Config} rather than GuizhanLib's {@code AddonConfig}:
 * both of {@code AddonConfig}'s constructors require a GuizhanLib {@code AbstractAddon} instance
 * (either directly, or indirectly via {@code AbstractAddon.getInstance()}), which this plugin never
 * creates (see {@link GeneticChickengineering}), so neither constructor is actually usable here.
 */
@Getter
public final class ConfigurationService {

    @Getter(AccessLevel.NONE)
    private final Config config;

    private boolean autoUpdate;
    private boolean debug;
    private boolean test;
    private String lang;
    private boolean displayResources;
    private int maxMutation;
    private int mutationRate;
    private int resourceFailRate;
    private int resourceBaseTime;
    private boolean painEnabled;
    private double painChance;
    private boolean painDeathEnabled;
    private int healRate;
    private boolean netherWaterEnabled;
    private boolean growthChamberEnabled;
    private int growthChamberTime;
    private boolean commandsEnabled;

    public ConfigurationService(GeneticChickengineering plugin) {
        config = new Config(plugin, "config.yml");
        reload();
    }

    public void reload() {
        config.reload();

        autoUpdate = config.getOrSetDefault("options.auto-update", true);
        debug = config.getOrSetDefault("options.debug", false);
        test = config.getOrSetDefault("options.test", false);
        lang = config.getOrSetDefault("options.lang", "en-US");
        displayResources = config.getOrSetDefault("options.display-resource-in-name", true);
        maxMutation = clampInt("options.max-mutation", 2, 1, 6);
        mutationRate = clampInt("options.mutation-rate", 30, 1, 100);
        resourceFailRate = clampInt("options.resource-fail-rate", 0, 0, 100);
        resourceBaseTime = clampInt("options.resource-base-time", 14, 14, 100);
        painEnabled = config.getOrSetDefault("options.enable-pain", false);
        painChance = clampDouble("options.pain-chance", 2d, 0d, 100d);
        painDeathEnabled = config.getOrSetDefault("options.pain-kills", false);
        healRate = clampInt("options.heal-rate", 2, 1, 120);
        netherWaterEnabled = config.getOrSetDefault("options.allow-nether-water", false);
        growthChamberEnabled = config.getOrSetDefault("options.enable-growth-chamber", false);
        growthChamberTime = clampInt("options.growth-chamber-time", 60, 1, 600);
        commandsEnabled = config.getOrSetDefault("commands.enabled", true);

        config.save();
    }

    public boolean isSubCommandEnabled(@Nonnull String subCommand) {
        return config.getOrSetDefault("commands.subcommands." + subCommand + ".enabled", true);
    }

    private int clampInt(@Nonnull String path, int defaultVal, int min, int max) {
        int val = config.getOrSetDefault(path, defaultVal);
        if (val < min || val > max) {
            val = defaultVal;
            config.setValue(path, val);
        }
        return val;
    }

    private double clampDouble(@Nonnull String path, double defaultVal, double min, double max) {
        double val = config.getOrSetDefault(path, defaultVal);
        if (val < min || val > max) {
            val = defaultVal;
            config.setValue(path, val);
        }
        return val;
    }
}
