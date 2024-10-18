package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class DeafenedEffect extends LivingEffectHolder implements IDamageEventListener {
    private int animTick = 0;
    private int alphaTick = 0;
    private boolean goUp = true;
    public DeafenedEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("deafened_effect"), FTZMobEffects.DEAFENED);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("animTick", animTick);
        tag.putInt("alphaTick", alphaTick);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        animTick = compoundTag.contains("animTick") ? compoundTag.getInt("animTick") : 0;
        alphaTick = compoundTag.contains("alphaTick") ? compoundTag.getInt("alphaTick") : 0;
    }

    @Override
    public void tick() {
        super.tick();
        animTick++;
        if (animTick >= 28) animTick = 0;
        if (goUp) alphaTick = Math.min(alphaTick + 7, 255); else alphaTick = Math.max(alphaTick - 7, 55);
        if (alphaTick >= 200) goUp = false; else if (alphaTick <= 0) goUp = true;
    }


    @Override
    public void onHit(LivingDamageEvent.Post event) {
        if (event.getSource().is(DamageTypes.SONIC_BOOM) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            LivingEffectHelper.makeDeaf(getEntity(), 200);
            LivingEffectHelper.microStun(getEntity());
            if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.RINGING.get()));
        }
    }

    public boolean renderDeaf() {
        return duration() > 0;
    }

    public int getAnimTick() {
        return animTick;
    }

    public int getAlphaTick() {
        return alphaTick;
    }
}
