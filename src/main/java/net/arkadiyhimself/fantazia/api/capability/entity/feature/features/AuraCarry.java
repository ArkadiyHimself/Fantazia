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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AuraCarry extends FeatureHolder {
    private final ArmorStand armorStand;
    private @Nullable AuraInstance<? extends Entity> auraInstance = null;
    public AuraCarry(ArmorStand entity) {
        super(entity);
        this.armorStand = entity;
    }
    @Override
    public String ID() {
        return "aura_carrier";
    }
    @Override
    public void onDeath() {
        if (auraInstance != null) auraInstance.discard();
    }
    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        if (auraInstance == null || auraInstance.getAura().getID() == null) return tag;
        tag.putString("aura", auraInstance.getAura().getID().toString());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        if (!tag.contains("aura")) return;
        ResourceLocation resourceLocation = new ResourceLocation(tag.getString("aura"));
        List<RegistryObject<BasicAura<?>>> auras = FantazicRegistry.AURAS.getEntries().stream().toList();
        for (RegistryObject<BasicAura<?>> registryObject : auras) if (resourceLocation.equals(registryObject.getId())) auraInstance = new AuraInstance<>(getEntity(), registryObject.get());
    }

    @Override
    public ArmorStand getEntity() {
        return armorStand;
    }
    public void setAura(BasicAura<? extends Entity> basicAura) {
        this.auraInstance = new AuraInstance<>(getEntity(), basicAura);
    }
}
