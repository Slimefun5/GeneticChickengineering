package net.guizhanss.gcereborn.core.adapters;

import java.util.List;

import com.google.gson.JsonObject;

import org.bukkit.entity.Animals;

import net.guizhanss.gcereborn.GeneticChickengineering;
import net.guizhanss.gcereborn.utils.EntityCompat;

/**
 * This class is a wholesale copy of TheBusyBiscuit's MobCapturer.
 */
public class AnimalsAdapter<T extends Animals> implements MobAdapter<T> {

    private final Class<T> entityClass;

    public AnimalsAdapter(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<String> getLore(JsonObject json) {
        List<String> lore = MobAdapter.super.getLore(json);

        boolean isBaby = json.get("baby").getAsBoolean();

        if (isBaby) {
            lore.add(GeneticChickengineering.getLocalization().getString("lores.chicken.baby"));
        }

        return lore;
    }

    @Override
    public JsonObject saveData(T entity) {
        JsonObject json = MobAdapter.super.saveData(entity);

        json.addProperty("baby", !entity.isAdult());
        json.addProperty("_age", entity.getAge());
        // getAgeLock/canBreed vary by version and getLoveModeTicks is 1.16+ - degrade gracefully.
        json.addProperty("_ageLock", EntityCompat.get(entity::getAgeLock, false));
        json.addProperty("_breedable", EntityCompat.get(entity::canBreed, false));
        json.addProperty("_loveModeTicks", EntityCompat.get(entity::getLoveModeTicks, 0));

        return json;
    }

    @Override
    public void apply(T entity, JsonObject json) {
        MobAdapter.super.apply(entity, json);

        entity.setAge(json.get("_age").getAsInt());
        EntityCompat.run(() -> entity.setLoveModeTicks(json.get("_loveModeTicks").getAsInt()));
        EntityCompat.run(() -> entity.setAgeLock(json.get("_ageLock").getAsBoolean()));
        EntityCompat.run(() -> entity.setBreed(json.get("_breedable").getAsBoolean()));
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

}
