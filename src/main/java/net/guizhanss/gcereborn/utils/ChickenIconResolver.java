package net.guizhanss.gcereborn.utils;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import net.guizhanss.gcereborn.items.chicken.ChickenTypes;

import io.github.thebusybiscuit.slimefun5.core.services.localization.ItemTextResolver;
import io.github.thebusybiscuit.slimefun5.core.services.localization.ItemTextBlocks;

/**
 * Resolves the dictionary chicken-icon items ({@code GCE_CHICKEN_ICON_<typing>}) for Slimefun's
 * per-viewer packet translation. Their name is composed at runtime from the chicken's product and the
 * id carries only the numeric typing, so it can't live in a static {@code items.yml}. Id-keyed
 * ({@code item} unused).
 *
 * @implNote Each language names the whole chicken ({@code chicken-icons.<PRODUCT>} in
 *           {@code chicken-icons.yml}) rather than filling a "{0} Chicken" template, because many
 *           languages compound or inflect the two words. A product with no entry falls back to the
 *           server-wide product name plus "Chicken", which is exactly what this rendered before.
 */
public final class ChickenIconResolver implements ItemTextResolver {

    private static final String PREFIX = "GCE_CHICKEN_ICON_";

    private final ResolverTexts texts;

    public ChickenIconResolver(@Nonnull ResolverTexts texts) {
        this.texts = texts;
    }

    @Override
    @Nullable
    public ItemTextBlocks resolve(@Nullable ItemStack item, String itemId, @Nullable String languageId) {
        if (!itemId.startsWith(PREFIX)) {
            return null;
        }

        int typing;

        try {
            typing = Integer.parseInt(itemId.substring(PREFIX.length()));
        } catch (NumberFormatException notOurId) {
            return null;
        }

        String productKey;
        String productName;

        try {
            productKey = ChickenTypes.getName(typing);
            productName = ChickenTypes.getDisplayName(typing);
        } catch (RuntimeException unknownTyping) {
            return null;
        }

        String chickenName = texts.get(languageId, "chicken-icons." + productKey);

        if (chickenName != null) {
            return ItemTextBlocks.name("&f" + chickenName);
        }

        if (productName == null || productName.isEmpty()) {
            productName = productKey;
        }

        return productName == null || productName.isEmpty() ? null : ItemTextBlocks.name("&f" + productName + " Chicken");
    }
}
