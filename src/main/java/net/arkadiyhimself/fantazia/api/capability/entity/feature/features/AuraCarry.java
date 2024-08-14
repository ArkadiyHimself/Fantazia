package net.arkadiyhimself.fantazia.api.capability.entity.feature.features;

import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AuraCarry extends FeatureHolder {
    private static final String ID = "aura_carrier:";
    private final ArmorStand armorStand;
    private AuraInstance<? extends Entity, ? extends Entity> auraInstance = null;
    public AuraCarry(ArmorStand entity) {
        super(entity);
        this.armorStand = entity;
    }
    public AuraInstance<? extends Entity, ? extends Entity> getAuraInstance() {
        return auraInstance;
    }
    @Override
    public void onDeath() {
        if (auraInstance != null) auraInstance.discard();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        if (auraInstance == null || getAuraInstance().getAura().getID() == null) return tag;
        tag.putString(ID + "aura", getAuraInstance().getAura().getID().toString());
        return tag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        if (!tag.contains(ID + "aura")) return;
        ResourceLocation resourceLocation = new ResourceLocation(tag.getString(ID + "aura"));
        List<RegistryObject<BasicAura<?,?>>> auras = FantazicRegistry.AURAS.getEntries().stream().toList();
        for (RegistryObject<BasicAura<?,?>> registryObject : auras) if (resourceLocation.equals(registryObject.getId())) auraInstance = new AuraInstance<>(getEntity(), (BasicAura<? extends Entity, Entity>) registryObject.get(), getEntity().level());
    }

    @Override
    public ArmorStand getEntity() {
        return armorStand;
    }
    @SuppressWarnings("unchecked")
    public void setAura(BasicAura<? extends Entity, ? extends Entity> basicAura) {
        this.auraInstance = new AuraInstance<>(getEntity(), (BasicAura<Entity, Entity>) basicAura, getEntity().level());
    }
}
