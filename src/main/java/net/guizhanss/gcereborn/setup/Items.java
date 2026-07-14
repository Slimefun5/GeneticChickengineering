package net.guizhanss.gcereborn.setup;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun5.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun5.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun5.libraries.xseries.XMaterial;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.utils.MaterialCompat;
import net.guizhanss.gcereborn.items.GCEItems;
import net.guizhanss.gcereborn.items.chicken.ChickenTypes;
import net.guizhanss.gcereborn.items.chicken.PocketChicken;
import net.guizhanss.gcereborn.items.common.ChickenNet;
import net.guizhanss.gcereborn.items.common.ResourceEgg;
import net.guizhanss.gcereborn.items.machines.ExcitationChamber;
import net.guizhanss.gcereborn.items.machines.GeneticSequencer;
import net.guizhanss.gcereborn.items.machines.GrowthChamber;
import net.guizhanss.gcereborn.items.machines.PrivateCoop;
import net.guizhanss.gcereborn.items.machines.RestorationChamber;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Items {

    public static void setup(@Nonnull GeneticChickengineering plugin) {
        new PocketChicken(
            Groups.MAIN,
            GCEItems.POCKET_CHICKEN,
            RecipeTypes.FROM_NET,
            new ItemStack[9]
        ).register(plugin);

        new ChickenNet(
            Groups.MAIN,
            GCEItems.CHICKEN_NET,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                null, new ItemStack(MaterialCompat.safe(XMaterial.STRING)), new ItemStack(MaterialCompat.safe(XMaterial.STRING)),
                null, new ItemStack(MaterialCompat.safe(XMaterial.STICK)), new ItemStack(MaterialCompat.safe(XMaterial.STRING)),
                null, new ItemStack(MaterialCompat.safe(XMaterial.STICK)), null
            }
        ).register(plugin);

        new ResourceEgg(
            Groups.MAIN,
            GCEItems.WATER_EGG,
            RecipeTypes.FROM_CHICKEN,
            MaterialCompat.safe(XMaterial.WATER),
            GeneticChickengineering.getConfigService().isNetherWaterEnabled()
        ).register(plugin);

        new ResourceEgg(
            Groups.MAIN,
            GCEItems.LAVA_EGG,
            RecipeTypes.FROM_CHICKEN,
            MaterialCompat.safe(XMaterial.LAVA),
            true
        ).register(plugin);

        new GeneticSequencer(
            Groups.MAIN,
            GCEItems.GENETIC_SEQUENCER,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(MaterialCompat.safe(XMaterial.OAK_PLANKS)), null, new ItemStack(MaterialCompat.safe(XMaterial.OAK_PLANKS)),
                new ItemStack(MaterialCompat.safe(XMaterial.COBBLESTONE)), new ItemStack(MaterialCompat.safe(XMaterial.OBSERVER)), new ItemStack(MaterialCompat.safe(XMaterial.COBBLESTONE)),
                new ItemStack(MaterialCompat.safe(XMaterial.COBBLESTONE)), SlimefunItems.ADVANCED_CIRCUIT_BOARD.item(), new ItemStack(MaterialCompat.safe(XMaterial.COBBLESTONE))
            }
        ).setCapacity(180).setEnergyConsumption(3).setProcessingSpeed(1).register(plugin);

        new ExcitationChamber(
            Groups.MAIN,
            GCEItems.EXCITATION_CHAMBER,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(MaterialCompat.safe(XMaterial.BLACKSTONE)), SlimefunItems.SMALL_CAPACITOR.item(), new ItemStack(MaterialCompat.safe(XMaterial.BLACKSTONE)),
                new ItemStack(MaterialCompat.safe(XMaterial.CHAIN)), null, new ItemStack(MaterialCompat.safe(XMaterial.CHAIN)),
                new ItemStack(MaterialCompat.safe(XMaterial.STONE)), SlimefunItems.ELECTRIC_MOTOR.item(), new ItemStack(MaterialCompat.safe(XMaterial.STONE))
            }
        ).setCapacity(250).setEnergyConsumption(5).setProcessingSpeed(1).register(plugin);

        new ExcitationChamber(
            Groups.MAIN,
            GCEItems.EXCITATION_CHAMBER_2,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                SlimefunItems.LEAD_INGOT.item(), SlimefunItems.BLISTERING_INGOT_3.item(), SlimefunItems.LEAD_INGOT.item(),
                SlimefunItems.BLISTERING_INGOT_3.item(), GCEItems.EXCITATION_CHAMBER.item(), SlimefunItems.BLISTERING_INGOT_3.item(),
                SlimefunItems.LEAD_INGOT.item(), SlimefunItems.BLISTERING_INGOT_3.item(), SlimefunItems.LEAD_INGOT.item()
            }
        ).setCapacity(1000).setEnergyConsumption(10).setProcessingSpeed(2).register(plugin);

        new ExcitationChamber(
            Groups.MAIN,
            GCEItems.EXCITATION_CHAMBER_3,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                SlimefunItems.MAGIC_LUMP_3.item(), SlimefunItems.NUCLEAR_REACTOR.item(), SlimefunItems.MAGIC_LUMP_3.item(),
                SlimefunItems.REINFORCED_PLATE.item(), GCEItems.EXCITATION_CHAMBER_2.item(), SlimefunItems.REINFORCED_PLATE.item(),
                SlimefunItems.MAGIC_LUMP_3.item(), SlimefunItems.URANIUM.item(), SlimefunItems.MAGIC_LUMP_3.item()
            }
        ).setCapacity(5000).setEnergyConsumption(50).setProcessingSpeed(10).register(plugin);

        new PrivateCoop(
            Groups.MAIN,
            GCEItems.PRIVATE_COOP,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                new ItemStack(MaterialCompat.safe(XMaterial.BIRCH_PLANKS)), new ItemStack(MaterialCompat.safe(XMaterial.BIRCH_PLANKS)), new ItemStack(MaterialCompat.safe(XMaterial.BIRCH_PLANKS)),
                new ItemStack(MaterialCompat.safe(XMaterial.JUKEBOX)), new ItemStack(MaterialCompat.safe(XMaterial.RED_BED)), new ItemStack(MaterialCompat.safe(XMaterial.POPPY)),
                new ItemStack(MaterialCompat.safe(XMaterial.BIRCH_PLANKS)), SlimefunItems.HEATING_COIL.item(), new ItemStack(MaterialCompat.safe(XMaterial.BIRCH_PLANKS))
            }
        ).setCapacity(30).setEnergyConsumption(1).setProcessingSpeed(1).register(plugin);

        if (GeneticChickengineering.getConfigService().isPainEnabled()) {
            new RestorationChamber(
                Groups.MAIN,
                GCEItems.RESTORATION_CHAMBER,
                RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[] {
                    new ItemStack(MaterialCompat.safe(XMaterial.PINK_TERRACOTTA)), new ItemStack(MaterialCompat.safe(XMaterial.PINK_TERRACOTTA)), new ItemStack(MaterialCompat.safe(XMaterial.PINK_TERRACOTTA)),
                    SlimefunItems.BANDAGE.item(), new ItemStack(MaterialCompat.safe(XMaterial.WHITE_BED)), SlimefunItems.MEDICINE.item(),
                    new ItemStack(MaterialCompat.safe(XMaterial.PINK_TERRACOTTA)), SlimefunItems.HEATING_COIL.item(), new ItemStack(MaterialCompat.safe(XMaterial.PINK_TERRACOTTA))
                }
            ).setCapacity(30).setEnergyConsumption(2).setProcessingSpeed(1).register(plugin);
        }

        if (GeneticChickengineering.getConfigService().isGrowthChamberEnabled()) {
            new GrowthChamber(
                Groups.MAIN,
                GCEItems.GROWTH_CHAMBER,
                RecipeType.ENHANCED_CRAFTING_TABLE,
                new ItemStack[] {
                    SlimefunItems.GOLD_24K.item(), SlimefunItems.TIN_CAN.item(), SlimefunItems.GOLD_24K.item(),
                    SlimefunItems.ELECTRIC_MOTOR.item(), new ItemStack(MaterialCompat.safe(XMaterial.HAY_BLOCK)), SlimefunItems.ELECTRIC_MOTOR.item(),
                    SlimefunItems.LEAD_INGOT.item(), SlimefunItems.FOOD_FABRICATOR.item(), SlimefunItems.LEAD_INGOT.item()
                }
            ).setCapacity(200).setEnergyConsumption(20).setProcessingSpeed(1).register(plugin);
        }

        ChickenTypes.registerChickens();
    }
}
