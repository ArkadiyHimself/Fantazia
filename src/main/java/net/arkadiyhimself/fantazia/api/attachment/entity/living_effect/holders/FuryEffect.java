package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class FuryEffect extends LivingEffectHolder implements IDamageEventListener {

    private int soundDelay = 20;
    private int veinTR = 0;
    private int backTR = 20;
    private boolean firstBeat = false;
    private boolean secondBeat = false;
    private int beat1 = 0;
    private int beat2 = 9;

    public FuryEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("fury_effect"), FTZMobEffects.FURY);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("veinTR", veinTR);
        tag.putInt("backTR", backTR);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        veinTR = compoundTag.contains("veinTR") ? compoundTag.getInt("veinTR") : 0;
        backTR = compoundTag.contains("backTR") ? compoundTag.getInt("backTR") : 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getEntity() instanceof ServerPlayer serverPlayer) || !isFurious()) return;
        if (veinTR > 0) veinTR--;
        if (soundDelay > 0) soundDelay--;
        backTR = Math.min(duration, 20);
        if (beat1 > 0 && firstBeat) beat1--;
        else if (beat1 <= 0) {
            beat1 = 8;
            firstBeat = false;
            secondBeat = true;
            veinTR = 8;
            PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.HEART_BEAT1.get()));
        }
        if (beat2 > 0 && secondBeat) beat2--;
        else if (beat2 <= 0) {
            beat2 = 9;
            firstBeat = true;
            secondBeat = false;
            veinTR = 9;
            PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.HEART_BEAT2.get()));
        }

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
    public void onHit(LivingIncomingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity livAtt && livAtt.hasEffect(FTZMobEffects.FURY)) {
            event.setAmount(event.getAmount() * 2);
            HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(getEntity().level());
            if (SpellHelper.hasSpell(livAtt, FTZSpells.DAMNED_WRATH) && healingSources != null) AdvancedHealing.tryHeal(livAtt, healingSources.lifesteal(getEntity()), 0.15f * event.getAmount());
        }

        if (duration() <= 0) return;
        float multiplier = SpellHelper.hasSpell(getEntity(), FTZSpells.DAMNED_WRATH) ? 1.5f : 2f;
        if (getEntity().hasEffect(FTZMobEffects.FURY)) event.setAmount(event.getAmount() * multiplier);
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
}
