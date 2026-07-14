package net.guizhanss.gcereborn.core.services;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun5.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun5.libraries.keys.NamespacedKey;
import io.github.thebusybiscuit.slimefun5.utils.SlimefunUtils;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.libs.guizhanlib.localization.MinecraftLocalization;
import net.guizhanss.gcereborn.libs.guizhanlib.utils.ChatUtil;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Localization + item-lookup service.
 * <p>
 * This extends GuizhanLib's {@code MinecraftLocalization} directly (a plain YAML-file localization
 * helper, no Slimefun coupling) rather than its {@code SlimefunLocalization} subclass: that subclass's
 * {@code getItem}/{@code getItemGroupItem}/{@code getRecipeType} methods return
 * {@code io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack} - the pre-fork type this
 * package no longer has - so invoking them would throw {@code NoClassDefFoundError}. The same lookup
 * behaviour (key/id -> name + lore -> item) is reimplemented here against the fork's own
 * {@link SlimefunItemStack} / {@link RecipeType}.
 */
@SuppressWarnings("ConstantConditions")
public final class LocalizationService extends MinecraftLocalization {

    private static final String KEY_NAME = ".name";
    private static final String KEY_LORE = ".lore";

    private String idPrefix = "";
    private String itemGroupKey = "categories";
    private String itemsKey = "items";
    private String recipesKey = "recipes";

    public LocalizationService(GeneticChickengineering plugin) {
        super(plugin);
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public void setItemGroupKey(String itemGroupKey) {
        this.itemGroupKey = itemGroupKey;
    }

    public void setItemsKey(String itemsKey) {
        this.itemsKey = itemsKey;
    }

    public void setRecipesKey(String recipesKey) {
        this.recipesKey = recipesKey;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public String getString(String key, Object... args) {
        return MessageFormat.format(getString(key), args);
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender sender, String messageKey, Object... args) {
        Preconditions.checkArgument(sender != null, "CommandSender cannot be null");
        Preconditions.checkArgument(messageKey != null, "Message key cannot be null");

        ChatUtil.send(sender, MessageFormat.format(getString("messages." + messageKey), args));
    }

    @ParametersAreNonnullByDefault
    public void sendActionbarMessage(Player p, String messageKey, Object... args) {
        Preconditions.checkArgument(p != null, "Player cannot be null");
        Preconditions.checkArgument(messageKey != null, "Message key cannot be null");

        String message = MessageFormat.format(getString("messages." + messageKey), args);

        BaseComponent[] components = TextComponent.fromLegacyText(ChatUtil.color(message));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    // --- Item lookups (fork-native replacement for SlimefunLocalization) ---

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemBy(String key, String id, Material material, String... extraLore) {
        Preconditions.checkArgument(material != null, "Material cannot be null");
        return getItemBy(key, id, new ItemStack(material), extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemBy(String key, String id, String texture, String... extraLore) {
        return getItemBy(key, id, SlimefunUtils.getCustomHead(texture), extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemBy(String key, String id, ItemStack itemStack, String... extraLore) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(id != null, "id cannot be null");
        Preconditions.checkArgument(itemStack != null, "ItemStack cannot be null");

        List<String> lore = getStringList(key + "." + id + KEY_LORE);
        Collections.addAll(lore, extraLore);

        return new SlimefunItemStack(
            (idPrefix + id).toUpperCase(Locale.ROOT),
            itemStack,
            getString(key + "." + id + KEY_NAME),
            lore.toArray(new String[0])
        );
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemGroupItem(String id, Material material) {
        return getItemBy(itemGroupKey, id, material);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemGroupItem(String id, String texture) {
        return getItemBy(itemGroupKey, id, texture);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemGroupItem(String id, ItemStack itemStack) {
        return getItemBy(itemGroupKey, id, itemStack);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItem(String id, Material material, String... extraLore) {
        return getItemBy(itemsKey, id, material, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItem(String id, String texture, String... extraLore) {
        return getItemBy(itemsKey, id, texture, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItem(String id, ItemStack itemStack, String... extraLore) {
        return getItemBy(itemsKey, id, itemStack, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public RecipeType getRecipeType(String id, Material material, String... extraLore) {
        return new RecipeType(
            new NamespacedKey(getPlugin(), id),
            getItemBy(recipesKey, id, material, extraLore)
        );
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public RecipeType getRecipeType(String id, String texture, String... extraLore) {
        return new RecipeType(
            new NamespacedKey(getPlugin(), id),
            getItemBy(recipesKey, id, texture, extraLore)
        );
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public RecipeType getRecipeType(String id, ItemStack itemStack, String... extraLore) {
        return new RecipeType(
            new NamespacedKey(getPlugin(), id),
            getItemBy(recipesKey, id, itemStack, extraLore)
        );
    }
}
