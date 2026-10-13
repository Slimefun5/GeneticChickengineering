package net.guizhanss.gcereborn.util;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun5.implementation.Slimefun;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.libs.guizhanlib.utils.ChatUtil;

/**
 * Resolves this addon's chat text for one viewer, from {@code languages/<lang>/messages.yml}.
 * <p>
 * This addon already had a language pack under {@code lang/}, but a pack is chosen once in
 * {@code config.yml} and so reads the same for everybody on the server. These keys resolve per
 * viewer instead, and the pack stays authoritative wherever an operator edited it.
 */
public final class Messages {

    private Messages() {}

    /**
     * Maps a {@code messages.yml} key to the language-pack key it took over from.
     *
     * @implNote The pack's positional {@code {0}} spelling is kept in {@code messages.yml} so an
     *           operator's edited line substitutes without being rewritten.
     */
    private static final Map<String, String> PACK_KEYS = packKeys(
            "command.no-permission", "messages.no-permission",
            "command.no-console", "messages.no-console",
            "command.invalid-dna-notation", "messages.invalid-dna-notation",
            "command.chance-result", "messages.chance-result",
            "command.calcchance-description", "commands.calcchance.description",
            "command.makechicken-description", "commands.makechicken.description");

    @Nonnull
    private static Map<String, String> packKeys(@Nonnull String... pairs) {
        Map<String, String> keys = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            keys.put(pairs[i], pairs[i + 1]);
        }
        return Collections.unmodifiableMap(keys);
    }

    /**
     * This addon's chat text in the viewer's own language, with {@code {0}}, {@code {1}}, ...
     * replaced by {@code args} in order.
     *
     * @implNote An operator who edited the line in their own language pack keeps their wording
     *           verbatim, compared against the SHIPPED copy of the pack they actually chose so that
     *           choosing a non-English pack is not mistaken for editing every line of it.
     *           <p>
     *           Only the pack's own value goes through {@link MessageFormat}; a translation is
     *           substituted by plain replacement. MessageFormat treats an apostrophe as a quote and
     *           silently stops substituting after one, which would break every language that writes
     *           {@code n'a} or {@code l'oggetto}.
     */
    @Nonnull
    public static String get(@Nullable CommandSender sender, @Nonnull String key, @Nonnull String english,
                             @Nonnull Object... args) {
        String packKey = PACK_KEYS.get(key);

        if (packKey != null && GeneticChickengineering.getLocalization().isCustomised(packKey)) {
            return MessageFormat.format(GeneticChickengineering.getLocalization().getString(packKey), args);
        }

        Player viewer = sender instanceof Player ? (Player) sender : null;
        String line = Slimefun.getAddonMessageService().getMessage(viewer,
                GeneticChickengineering.getInstance(), key, english);

        for (int i = 0; i < args.length; i++) {
            line = line.replace("{" + i + "}", String.valueOf(args[i]));
        }

        return line;
    }

    public static void send(@Nonnull CommandSender recipient, @Nonnull ChatColor colour,
                            @Nonnull String key, @Nonnull String english, @Nonnull Object... args) {
        ChatUtil.send(recipient, colour + get(recipient, key, english, args));
    }
}
