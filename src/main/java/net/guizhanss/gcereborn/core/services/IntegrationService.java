package net.guizhanss.gcereborn.core.services;

import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;

import org.bukkit.entity.Chicken;
import org.bukkit.plugin.Plugin;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.integrations.wildstacker.EntityStackListener;

import lombok.Getter;

/**
 * StackMob is integrated purely through reflection rather than a compile-time dependency: its
 * published jars (5.8.10 here) are Java-11+ class files, which a Java-8 {@code javac} cannot even
 * read as a compile dependency ("class file has wrong version 55.0, should be 52.0") - the same
 * problem GuizhanLib-api's Java-16 jar caused (see {@link GeneticChickengineering}'s javadoc).
 * WildStackerAPI happens to still publish Java-8-compatible class files, so it stays a normal
 * {@code compileOnly} dependency.
 */
@Getter
public final class IntegrationService {

    private final GeneticChickengineering plugin;

    private final boolean stackMobEnabled;
    private final boolean wildStackerEnabled;

    private Plugin stackMobInst;

    public IntegrationService(GeneticChickengineering plugin) {
        this.plugin = plugin;

        stackMobEnabled = isEnabled("StackMob");
        wildStackerEnabled = isEnabled("WildStacker");

        if (stackMobEnabled) {
            stackMobInst = plugin.getServer().getPluginManager().getPlugin("StackMob");
        }

        if (wildStackerEnabled) {
            new EntityStackListener(plugin);
        }
    }

    private boolean isEnabled(@Nonnull String pluginName) {
        boolean result = plugin.getServer().getPluginManager().isPluginEnabled(pluginName);
        if (result) {
            GeneticChickengineering.log(Level.INFO,
                GeneticChickengineering.getLocalization().getString("console.load.integration", pluginName));
        }
        return result;
    }

    public void captureChicken(@Nonnull Chicken chicken) {
        try {
            if (stackMobEnabled) {
                captureViaStackMob(chicken);
            } else if (wildStackerEnabled) {
                StackedEntity stackedEntity = WildStackerAPI.getStackedEntity(chicken);
                if (stackedEntity != null && stackedEntity.getStackAmount() > 1) {
                    stackedEntity.decreaseStackAmount(1, true);
                } else {
                    chicken.remove();
                }
            } else {
                chicken.remove();
            }
        } catch (Exception e) {
            GeneticChickengineering.log(Level.SEVERE, e, "An error has occurred while capturing chicken");
            chicken.remove();
        }
    }

    /**
     * Mirrors {@code stackMobInst.getEntityManager().getStackEntity(chicken)} +
     * {@code stackEntity.getSize()}/{@code incrementSize(-1)} purely via reflection, see the class
     * javadoc for why StackMob can't be a compile dependency.
     */
    private void captureViaStackMob(@Nonnull Chicken chicken) throws ReflectiveOperationException {
        Method getEntityManager = stackMobInst.getClass().getMethod("getEntityManager");
        Object entityManager = getEntityManager.invoke(stackMobInst);

        Method getStackEntity = entityManager.getClass().getMethod("getStackEntity", org.bukkit.entity.LivingEntity.class);
        Object stackEntity = getStackEntity.invoke(entityManager, chicken);

        if (stackEntity != null) {
            int size = (Integer) stackEntity.getClass().getMethod("getSize").invoke(stackEntity);
            if (size > 1) {
                stackEntity.getClass().getMethod("incrementSize", int.class).invoke(stackEntity, -1);
                return;
            }
        }
        chicken.remove();
    }
}
