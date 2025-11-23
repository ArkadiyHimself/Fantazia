package net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments;

import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class BifrostHealthHolder implements INBTSerializable<CompoundTag>, IDamageEventListener, ISyncEveryTick {

    private final IAttachmentHolder holder;
    private float toDamage;
    private int delay;

    public BifrostHealthHolder(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public void tick() {
        if (delay > 0) {
            delay--;
            if (delay == 0 && holder instanceof LivingEntity entity) {
                LevelAttributesHelper.hurtEntity(entity, toDamage, DamageSourcesHolder::removal);
                this.toDamage = 0;
            }
        } else {
            this.toDamage = Math.max(0, toDamage - 0.05f);
        }
    }

    public void addBifrost(float amount) {
        if (!(holder instanceof LivingEntity living)) return;
        this.toDamage = Math.min(toDamage + amount, living.getMaxHealth());
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (source.is(FTZDamageTypeTags.IGNORED_BY_BIFROST)) return;
        this.delay = 21;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", toDamage);
        tag.putInt("delay", delay);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.toDamage = tag.getFloat("damage");
        this.delay = tag.getInt("delay");
    }

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", toDamage);
        tag.putInt("delay", delay);
        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        this.toDamage = tag.getFloat("damage");
        this.delay = tag.getInt("delay");
    }
}
