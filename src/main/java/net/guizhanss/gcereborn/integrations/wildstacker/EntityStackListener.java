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

        if (!sourceHasDna && !targetHasDna) {
            return;
        }

        // Only one side carries DNA - block the merge so the tagged chicken keeps its identity.
        if (sourceHasDna != targetHasDna) {
            e.setCancelled(true);
            return;
        }

        // Both are tagged: block the merge only when their DNA differs.
        if (!PdcCompat.getString(source, Keys.CHICKEN_DNA).equals(PdcCompat.getString(target, Keys.CHICKEN_DNA))) {
            e.setCancelled(true);
        }
    }
}
