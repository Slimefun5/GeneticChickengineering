package net.guizhanss.gcereborn.utils;


import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import net.guizhanss.gcereborn.items.chicken.ChickenTypes;

import io.github.thebusybiscuit.slimefun5.core.services.localization.ItemTextResolver;
import io.github.thebusybiscuit.slimefun5.core.services.localization.ItemTextBlocks;

/**
 * Resolves the dictionary chicken-icon items ({@code GCE_CHICKEN_ICON_<typing>}) for Slimefun's
 * per-viewer packet translation. Their name is composed at runtime from the chicken's product
 * ({@code <product> Chicken}) and the id carries only the numeric typing, so it can't live in a
 * static {@code items.yml}. Id-keyed ({@code item} unused); reproduces {@code ChickenUtils}' exact
 * naming. English baseline for now.
 */
public final class ChickenIconResolver implements ItemTextResolver {

    private static final String PREFIX = "GCE_CHICKEN_ICON_";

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

        String productName;

        try {
            productName = ChickenTypes.getDisplayName(typing);

            if (productName == null || productName.isEmpty()) {
                productName = ChickenTypes.getName(typing);
            }
        } catch (IndexOutOfBoundsException unknownTyping) {
            return null;
        }

        if (productName == null || productName.isEmpty()) {
            return null;
        }

        return ItemTextBlocks.name("&f" + productName + " Chicken");
    }
}
