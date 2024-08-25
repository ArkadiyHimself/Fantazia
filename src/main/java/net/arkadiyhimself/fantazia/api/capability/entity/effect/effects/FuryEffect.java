package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class FuryEffect extends EffectHolder implements IDamageReacting {
    private int soundDelay = 20;
    private int veinTR = 0;
    private int backTR = 20;
    private boolean firstBeat = false;
    private boolean secondBeat = false;
    private int beat1 = 0;
    private int beat2 = 9;
    public FuryEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.FURY.get());
    }

    public int getVeinTR() {
        return veinTR;
    }

    public int getBackTR() {
        return backTR;
    }

    public boolean isFurious() {
        return duration > 0;
    }
    @Override
    public void tick() {
        super.tick();
        if (!(getOwner() instanceof ServerPlayer serverPlayer) || !isFurious()) return;
        if (veinTR > 0) veinTR--;
        if (soundDelay > 0) soundDelay--;
        backTR = Math.min(duration, 20);
        if (beat1 > 0 && firstBeat) beat1--;
        else if (beat1 <= 0) {
            beat1 = 8;
            firstBeat = false;
            secondBeat = true;
            veinTR = 8;
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.HEART_BEAT1.get()), serverPlayer);
        }
        if (beat2 > 0 && secondBeat) beat2--;
        else if (beat2 <= 0) {
            beat2 = 9;
            firstBeat = true;
            secondBeat = false;
            veinTR = 9;
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.HEART_BEAT2.get()), serverPlayer);
        }

    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = super.serialize(toDisk);
        tag.putInt("veinTR", veinTR);
        tag.putInt("backTR", backTR);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        super.deserialize(tag, fromDisk);
        veinTR = tag.contains("veinTR") ? tag.getInt("veinTR") : 0;
        backTR = tag.contains("backTR") ? tag.getInt("backTR") : 0;
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        soundDelay = 20;
        veinTR = 0;
        firstBeat = false;
        secondBeat = false;
        beat1 = 0;
        beat2 = 0;
    }
    @Override
    public void onHit(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity livAtt && livAtt.hasEffect(FTZMobEffects.FURY.get())) {
            event.setAmount(event.getAmount() * 2);
            HealingSources healingSources = LevelCapHelper.getHealingSources(target.level());
            if (SpellHelper.hasSpell(livAtt, FTZSpells.DAMNED_WRATH.get()) && healingSources != null) AdvancedHealing.heal(livAtt, healingSources.lifesteal(target), 0.15f * event.getAmount());
        }

        if (getDur() <= 0) return;
        float multiplier = SpellHelper.hasSpell(getOwner(), FTZSpells.DAMNED_WRATH.get()) ? 1.5f : 2f;
        if (getOwner().hasEffect(FTZMobEffects.FURY.get())) event.setAmount(event.getAmount() * multiplier);
    }
}
