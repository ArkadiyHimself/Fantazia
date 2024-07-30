package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class DeafenedEffect extends EffectHolder implements IDamageReacting {
    public static List<SoundEvent> IGNORED = new ArrayList<>(){{
        add(SoundEvents.GENERIC_EXPLODE);
        add(SoundEvents.WARDEN_SONIC_BOOM);
        add(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
        add(FTZSoundEvents.HEART_BEAT1);
        add(FTZSoundEvents.HEART_BEAT2);
        add(FTZSoundEvents.DASH1_RECH);
        add(FTZSoundEvents.DASH2_RECH);
        add(FTZSoundEvents.DASH3_RECH);
        add(FTZSoundEvents.RINGING);
        add(FTZSoundEvents.BLOODLUST_AMULET);
        add(FTZSoundEvents.FURY_DISPEL);
        add(FTZSoundEvents.FURY_PROLONG);
        add(FTZSoundEvents.DOOMED);
        add(FTZSoundEvents.DENIED);
    }};
    private int animTick = 0;
    private int alphaTick = 0;
    private boolean goUp = true;
    public static final List<EntityType<? extends LivingEntity>> AFFECTED = new ArrayList<>(){{
        add(EntityType.WARDEN);
        add(EntityType.PLAYER);
    }};
    public DeafenedEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DEAFENED);
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
        if (alphaTick >= 200) goUp = false;
        if (alphaTick <= 0) goUp = true;

    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "animTick", animTick);
        tag.putInt(ID + "alphaTick", alphaTick);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        animTick = tag.contains(ID + "animTick") ? tag.getInt(ID + "animTick") : 0;
        alphaTick = tag.contains(ID + "alphaTick") ? tag.getInt(ID + "alphaTick") : 0;
    }
    @Override
    public void onHit(LivingDamageEvent event) {
        if (event.getSource().is(DamageTypes.SONIC_BOOM) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            getOwner().addEffect(new MobEffectInstance(FTZMobEffects.DEAFENED, 200, 0, true, false, true));
            if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.RINGING), serverPlayer);
        }
    }
}
