package net.arkadiyhimself.fantazia.advanced.capability.entity.feature.features;

import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class AuraCarry extends FeatureHolder {
    private static final String ID = "aura_carrier:";
    private final ArmorStand armorStand;
    private AuraInstance<? extends Entity, ? extends Entity> auraInstance = null;
    public AuraCarry(ArmorStand entity) {
        super(entity);
        this.armorStand = entity;
    }
    public AuraInstance<? extends Entity, ? extends Entity> getAura() {
        return auraInstance;
    }
    @Override
    public void onDeath() {
        if (auraInstance != null) auraInstance.discard();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        if (auraInstance == null || getAura().getAura().getMapKey() == null) return tag;
        tag.putString(ID + "aura", getAura().getAura().getMapKey().toString());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        if (!tag.contains(ID + "aura")) return;
        ResourceLocation resourceLocation = new ResourceLocation(tag.getString(ID + "aura"));
        if (BasicAura.AURAS.containsKey(resourceLocation)) auraInstance = new AuraInstance<>(getEntity(), (BasicAura<? extends Entity, ? super Entity>) BasicAura.AURAS.get(resourceLocation), getEntity().level());
    }

    @Override
    public ArmorStand getEntity() {
        return armorStand;
    }

    public void setAura(BasicAura<? extends Entity, ? extends Entity> basicAura) {
        this.auraInstance = new AuraInstance<>(getEntity(), (BasicAura<Entity, Entity>) basicAura, getEntity().level());
    }
}
