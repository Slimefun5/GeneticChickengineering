package net.guizhanss.gcereborn;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun5.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun5.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun5.libraries.dough.updater.BlobBuildUpdater;

import net.guizhanss.gcereborn.core.commands.GCECommand;
import net.guizhanss.gcereborn.core.services.ConfigurationService;
import net.guizhanss.gcereborn.core.services.IntegrationService;
import net.guizhanss.gcereborn.core.services.LocalizationService;
import net.guizhanss.gcereborn.libs.guizhanlib.Scheduler;
import net.guizhanss.gcereborn.setup.Items;
import net.guizhanss.gcereborn.setup.Researches;

import org.bstats.bukkit.Metrics;

/**
 * Main plugin class.
 * <p>
 * Note: unlike the upstream Reborn build, this does <b>not</b> extend GuizhanLib's
 * {@code AbstractAddon}. That class {@code implements io.github.thebusybiscuit.slimefun4.api.SlimefunAddon},
 * a package this fork renamed to {@code slimefun5}; loading it would throw
 * {@code NoClassDefFoundError} immediately. GuizhanLib-api's jar is also compiled for Java 16 (class
 * file version 60), which a Java-8 {@code javac} cannot even read as a compile-time dependency - so
 * this addon no longer depends on the GuizhanLib-api artifact at all. Instead this plugin implements
 * the fork's {@link SlimefunAddon} directly (same approach as SMG/SimpleUtils) and vendors the small,
 * Slimefun-independent pieces of GuizhanLib it used ({@link Scheduler}, the command framework,
 * chat/version/localization utils) as plain Java-8 source under
 * {@code net.guizhanss.gcereborn.libs.guizhanlib}.
 */
public class GeneticChickengineering extends JavaPlugin implements SlimefunAddon {

    private static final String DEFAULT_LANG = "en-US";
    private static final String GITHUB_USER = "ybw0014";
    private static final String GITHUB_REPO = "GeneticChickengineering-Reborn";
    private static final String GITHUB_BRANCH = "master";

    private static GeneticChickengineering instance;

    private ConfigurationService configService;
    private LocalizationService localization;
    private IntegrationService integrationService;
    private Scheduler scheduler;
    private boolean debugEnabled = false;

    @Nonnull
    public static GeneticChickengineering getInstance() {
        return instance;
    }

    @Nonnull
    public static ConfigurationService getConfigService() {
        return instance.configService;
    }

    @Nonnull
    public static LocalizationService getLocalization() {
        return instance.localization;
    }

    @Nonnull
    public static IntegrationService getIntegrationService() {
        return instance.integrationService;
    }

    @Nonnull
    public static Scheduler getScheduler() {
        return instance.scheduler;
    }

    public static void debug(@Nonnull String message, @Nonnull Object... args) {
        Preconditions.checkNotNull(message, "message cannot be null");

        if (instance.debugEnabled) {
            instance.getLogger().log(Level.INFO, "[DEBUG] " + message, args);
        }
    }

    public static void log(@Nonnull Level level, @Nonnull String message, @Nonnull Object... args) {
        instance.getLogger().log(level, message, args);
    }

    public static void log(@Nonnull Level level, @Nonnull Throwable throwable, @Nonnull String message, @Nonnull Object... args) {
        instance.getLogger().log(level, message, throwable);
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        File datadir = this.getDataFolder();
        if (!datadir.exists()) {
            datadir.mkdirs();
        }

        scheduler = new Scheduler(this);

        configService = new ConfigurationService(this);
        debugEnabled = configService.isDebug();

        log(Level.INFO, "Loading language...");
        String lang = configService.getLang();
        localization = new LocalizationService(this);
        localization.addLanguage(lang);
        if (!lang.equals(DEFAULT_LANG)) {
            localization.addLanguage(DEFAULT_LANG);
        }
        localization.setIdPrefix("GCE_");
        log(Level.INFO, localization.getString("console.load.language"), lang);

        log(Level.INFO, localization.getString("console.load.items"));
        Items.setup(this);

        // Contribute this addon's per-language item translations (languages/<lang>/items.yml).
        Slimefun.getItemTranslationService().registerTranslations(this);

        // The chicken-icon dictionary items are named at runtime from the chicken product, keyed only on
        // a numeric typing, so they can't live in items.yml - a resolver reproduces their display.
        Slimefun.getItemTranslationService().registerResolver(new net.guizhanss.gcereborn.utils.ChickenIconResolver());

        log(Level.INFO, localization.getString("console.load.researches"));
        Researches.setup();

        if (configService.isCommandsEnabled()) {
            PluginCommand command = getCommand("geneticchickengineering");
            if (command == null) {
                log(Level.SEVERE, localization.getString("console.load.commands-fail"));
            } else {
                new GCECommand(command).register();
            }
        }

        log(Level.INFO, localization.getString("console.load.integrations"));
        integrationService = new IntegrationService(this);

        setupMetrics();

        if (configService.isAutoUpdate()) {
            autoUpdate();
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void setupMetrics() {
        // Consolidated metrics: only start our own bStats if the server opted out (metrics.disable-addon-metrics = false).
        if (Slimefun.getCfg().contains("metrics.disable-addon-metrics") && !Slimefun.getCfg().getBoolean("metrics.disable-addon-metrics")) {
            new Metrics(this, 20243);
        }
    }

    protected void autoUpdate() {
        String version = getDescription().getVersion();
        if (version.startsWith("Dev")) {
            new BlobBuildUpdater(this, getFile(), GITHUB_REPO).start();
        } else if (version.startsWith("Build")) {
            // Only the optional companion "GuizhanLibPlugin" updater is used here (via reflection, so
            // it is never a compile/runtime dependency of this addon). The upstream fallback path -
            // GuizhanLib-api's own GuizhanBuildsUpdater - is unavailable now that this addon no longer
            // bundles that jar (see the class javadoc); "Build"-tagged versions without
            // GuizhanLibPlugin installed simply won't self-update.
            try {
                Class<?> clazz = Class.forName("net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater");
                Method updaterStart = clazz.getDeclaredMethod("start", Plugin.class, File.class, String.class, String.class, String.class);
                updaterStart.invoke(null, this, getFile(), GITHUB_USER, GITHUB_REPO, GITHUB_BRANCH);
            } catch (Exception ignored) {
                log(Level.WARNING, "Auto-update for \"Build\" versions requires GuizhanLibPlugin to be installed.");
            }
        }
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nonnull
    @Override
    public String getBugTrackerURL() {
        return "https://github.com/" + GITHUB_USER + "/" + GITHUB_REPO + "/issues";
    }
}
