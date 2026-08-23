package net.guizhanss.gcereborn.setup;

import io.github.thebusybiscuit.slimefun5.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.utils.Heads;
import net.guizhanss.gcereborn.utils.Keys;
import net.guizhanss.gcereborn.utils.MaterialCompat;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Groups {

    public static final ItemGroup MAIN = new ItemGroup(
        Keys.get("genetic_chickengineering"),
        GeneticChickengineering.getLocalization().getItemGroupItem(
            "ICON",
            Heads.CHICKEN.getTexture()
        )
    );

    public static final ItemGroup DICTIONARY = new ItemGroup(
        Keys.get("genetic_chickengineering_chickens"),
        GeneticChickengineering.getLocalization().getItemGroupItem(
            "DIRECTORY_ICON",
            MaterialCompat.safe(XMaterial.BLAST_FURNACE)
        )
    );

    /**
     * Places this addon's items in the shared guide categories. One call per group is enough:
     * the guide reads a group's category for every item in it (see ItemTypeClassifier), so items
     * no longer fall back to Misc.
     */
    static {
        MAIN.setCategory("food");
        DICTIONARY.setCategory("misc");
    }
}
