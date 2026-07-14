package net.guizhanss.gcereborn.utils;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class GuiItems {

    public static final ItemStack BLACK_PANE = CustomItemStack.create(MaterialCompat.safe(XMaterial.BLACK_STAINED_GLASS_PANE), " ");
}
