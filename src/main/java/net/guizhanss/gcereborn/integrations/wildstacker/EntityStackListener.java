package net.guizhanss.gcereborn.integrations.wildstacker;

import com.bgsoftware.wildstacker.api.events.EntityStackEvent;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun5.utils.compatibility.PdcCompat;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.utils.Keys;

public class EntityStackListener implements Listener {

    public EntityStackListener(GeneticChickengineering plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityStack(EntityStackEvent e) {
        if (e.getEntity().getType() != EntityType.CHICKEN || e.getTarget().getType() != EntityType.CHICKEN) {
            return;
        }
        Chicken source = (Chicken) e.getEntity().getLivingEntity();
        Chicken target = (Chicken) e.getTarget().getLivingEntity();

        boolean sourceHasDna = PdcCompat.has(source, Keys.CHICKEN_DNA, "STRING");
        boolean targetHasDna = PdcCompat.has(target, Keys.CHICKEN_DNA, "STRING");

        // both have no dna data, no need to handle.
        if (!sourceHasDna && !targetHasDna) {
            return;
        }

        // one of them has dna data, cancel merging.
        if (sourceHasDna != targetHasDna) {
            e.setCancelled(true);
            return;
        }

        // now both have dna data, check if they are different.
        // if so, cancel merging.
        if (!PdcCompat.getString(source, Keys.CHICKEN_DNA).equals(PdcCompat.getString(target, Keys.CHICKEN_DNA))) {
            e.setCancelled(true);
        }
    }
}
