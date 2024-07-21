package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.advanced.capacity.SpellHandler.Spells;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.advanced.healing.HealingTypes;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.arkadiyhimself.fantazia.registry.SoundRegistry;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.util.interfaces.IDamageReacting;
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
        super(owner, MobEffectRegistry.FURY.get());
    }

    public int getVeinTR() {
        return veinTR;
    }

    public int getBackTR() {
        return backTR;
    }

    public boolean hasFury() {
        return duration > 0;
    }
    @Override
    public void tick() {
        super.tick();
        if (!(getOwner() instanceof ServerPlayer serverPlayer) || !hasFury()) return;
        if (veinTR > 0) veinTR--;
        if (soundDelay > 0) soundDelay--;

        if (beat1 > 0 && firstBeat) beat1--;
        else if (beat1 <= 0) {
            beat1 = 10;
            firstBeat = false;
            secondBeat = true;
            veinTR = 10;
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.HEART_BEAT1.get()), serverPlayer);
        }

        if (beat2 > 0 && secondBeat) beat2--;
        else if (beat2 <= 0) {
            beat2 = 9;
            firstBeat = true;
            secondBeat = false;
            veinTR = 10;
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.HEART_BEAT2.get()), serverPlayer);
        }

    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "veinTR", veinTR);
        tag.putInt(ID + "backTR", backTR);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        veinTR = tag.contains(ID + "veinTR") ? tag.getInt(ID + "veinTR") : 0;
        backTR = tag.contains(ID + "backTR") ? tag.getInt(ID + "backTR") : 0;
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        soundDelay = 20;
        backTR = Math.min(instance.getDuration(), 20);
        veinTR = 0;
        firstBeat = false;
        secondBeat = false;
        beat1 = 0;
        beat2 = 0;
    }
    @Override
    public void onHit(LivingDamageEvent event) {
        if (getDur() <= 0) return;

        float multiplier = WhereMagicHappens.Abilities.hasSpell(getOwner(), Spells.DAMNED_WRATH) ? 1.5f : 2f;
        event.setAmount(event.getAmount() * multiplier);

        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity livAtt && livAtt.hasEffect(MobEffectRegistry.FURY.get())) {
            event.setAmount(event.getAmount() * 2);
            if (WhereMagicHappens.Abilities.hasSpell(livAtt, Spells.DAMNED_WRATH)) {
                float heal = 0.15f * event.getAmount();
                AdvancedHealing.heal(livAtt, new HealingSource(HealingTypes.LIFESTEAL), heal);
            }
        }
    }
}
