package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.UUID;

public class PuppeteeredEffect extends LivingEffectHolder {

    private @Nullable UUID master = null;
    private @Nullable UUID puppet = null;

    public PuppeteeredEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("puppeteered"), FTZMobEffects.PUPPETEERED);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        if (master != null) tag.putUUID("master", master);
        if (puppet != null) tag.putUUID("puppet", puppet);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);

        if (compoundTag.hasUUID("master")) this.master = compoundTag.getUUID("master");
        if (compoundTag.hasUUID("puppet")) this.puppet = compoundTag.getUUID("puppet");
    }

    @Override
    public void ended() {
        super.ended();
        if (getEntity().level() instanceof ServerLevel serverLevel && master != null) {
            Entity entity = serverLevel.getEntity(master);
            if (entity instanceof LivingEntity livingEntity) LivingEffectGetter.acceptConsumer(livingEntity, PuppeteeredEffect.class, puppeteeredEffect -> puppeteeredEffect.removePuppet(getEntity().getUUID()));
        }
        this.master = null;
    }

    public boolean isPuppeteeredBy(LivingEntity entity) {
        return entity.getUUID().equals(master);
    }

    public void enslave(LivingEntity master) {
        this.master = master.getUUID();
    }

    public void givePuppet(UUID newPuppet) {
        if (newPuppet.equals(puppet) || !(getEntity().level() instanceof ServerLevel serverLevel)) return;
        if (puppet != null) {
            Entity entity = serverLevel.getEntity(puppet);
            if (entity != null) entity.kill();
        }
        this.puppet = newPuppet;
    }

    public void removePuppet(UUID puppet) {
        if (puppet == this.puppet) this.puppet = null;
    }
}
