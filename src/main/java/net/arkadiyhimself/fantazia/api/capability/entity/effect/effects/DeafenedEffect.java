package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class DeafenedEffect extends EffectHolder implements IDamageReacting {
    private int animTick = 0;
    private int alphaTick = 0;
    private boolean goUp = true;
    public DeafenedEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DEAFENED.get());
    }
    public boolean renderDeaf() {
        return getDur() > 0;
    }
    public int getAnimTick() {
        return animTick;
    }
    public int getAlphaTick() {
        return alphaTick;
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
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = super.serialize(toDisk);
        tag.putInt("animTick", animTick);
        tag.putInt("alphaTick", alphaTick);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        super.deserialize(tag, fromDisk);
        animTick = tag.contains("animTick") ? tag.getInt("animTick") : 0;
        alphaTick = tag.contains("alphaTick") ? tag.getInt("alphaTick") : 0;
    }
    @Override
    @SuppressWarnings("ConstantConditions")
    public void onHit(LivingDamageEvent event) {
        if (event.getSource().is(DamageTypes.SONIC_BOOM) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            EffectHelper.makeDeaf(getOwner(), 200);
            EffectHelper.microStun(getOwner());
            if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.RINGING.get()), serverPlayer);
        }
    }
}
