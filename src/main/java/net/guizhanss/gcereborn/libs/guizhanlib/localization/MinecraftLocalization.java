package net.guizhanss.gcereborn.libs.guizhanlib.localization;

import net.guizhanss.gcereborn.libs.guizhanlib.utils.ChatUtil;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * An extended {@link Localization} that colors localized strings.
 * <p>
 * Java-8 port of GuizhanLib's {@code MinecraftLocalization} - see the note on
 * {@link net.guizhanss.gcereborn.libs.guizhanlib.commands.AbstractCommand}.
 *
 * @author ybw0014 (original), downleveled for Java 8
 */
public class MinecraftLocalization extends Localization {

    @ParametersAreNonnullByDefault
    public MinecraftLocalization(JavaPlugin plugin) {
        super(plugin);
    }

    @ParametersAreNonnullByDefault
    public MinecraftLocalization(JavaPlugin plugin, String folderName) {
        super(plugin, folderName);
    }

    @ParametersAreNonnullByDefault
    public MinecraftLocalization(JavaPlugin plugin, String folderName, String langFile) {
        super(plugin, folderName, langFile);
    }

    @Nonnull
    @Override
    public String getString(@Nonnull String path) {
        return ChatUtil.color(super.getString(path));
    }

    @Nonnull
    @Override
    public List<String> getStringList(@Nonnull String path) {
        return ChatUtil.color(super.getStringList(path));
    }

    @Nonnull
    @Override
    public String[] getStringArray(@Nonnull String path) {
        return getStringList(path).toArray(new String[0]);
    }
}
