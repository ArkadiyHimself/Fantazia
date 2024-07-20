package net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Auras.AuraInstance;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Auras.BasicAura;
import net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager.Cleansing;
import net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager.EffectCleansing;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.Items.MagicCasters.SpellCaster;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CommonData extends EntityCapability {
    public CommonData(Entity entity) { super(entity); }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(entity.getId(), AttachCommonData.COMMON_DATA_SYNC_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tick", tick);
        tag.putInt("jumpingTick", jumpingTick);
        tag.putInt("bleedingSoundCD", bleedingSoundCD);
        tag.putInt("ancientFlameTicks", ancientFlameTicks);

        tag.putInt("maxDurationFreeze", maxDurationFreeze);
        tag.putInt("currDurationFreeze", currDurationFreeze);
        tag.putInt("maxDurationDisarm", maxDurationDisarm);
        tag.putInt("currDurationDisarm", currDurationDisarm);
        tag.putBoolean("hasDeafening", hasDeafening);
        tag.putInt("damageTicks", damageTicks);
        tag.putBoolean("furious", furious);
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        tick = nbt.contains("tick") ? nbt.getInt("tick") : 0;
        jumpingTick = nbt.contains("jumpingTick") ? nbt.getInt("jumpingTick") : 0;
        bleedingSoundCD = nbt.contains("bleedingSoundCD") ? nbt.getInt("bleedingSoundCD") : 0;
        ancientFlameTicks = nbt.contains("ancientFlameTicks") ? nbt.getInt("ancientFlameTicks") : 0;

        maxDurationFreeze = nbt.contains("maxDurationFreeze") ? nbt.getInt("maxDurationFreeze") : 1;
        currDurationFreeze = nbt.contains("currDurationFreeze") ? nbt.getInt("currDurationFreeze") : 0;
        maxDurationDisarm = nbt.contains("maxDurationDisarm") ? nbt.getInt("maxDurationDisarm") : 1;
        currDurationDisarm = nbt.contains("currDurationDisarm") ? nbt.getInt("currDurationDisarm") : 0;
        hasDeafening = nbt.contains("hasDeafening") && nbt.getBoolean("hasDeafening");
        damageTicks = nbt.contains("damageTicks") ? nbt.getInt("damageTicks") : 0;
        furious = nbt.contains("furious") && nbt.getBoolean("furious");
    }
    public void tick() {

        if (entity.level().isClientSide()) return;
        tick++;
        if (jumpUp) { jumpingTick++; }
        else { jumpingTick--; }
        if (jumpingTick <= 0) jumpUp = true;
        if (jumpingTick >= 100) jumpUp = false;
        if (tick >= 100) tick = 0;
        if (damageTicks >= 0) damageTicks--;
        ancientFlameTicks = Math.max(0, ancientFlameTicks - 1);
        if (ancientFlameTicks > 0) entity.hurt(new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.ANCIENT_BURNING)), 1.5f);
        bleedingSoundCD = Math.max(0, bleedingSoundCD - 1);
        List<AuraInstance<Entity, Entity>> affectingAuras = WhereMagicHappens.Abilities.getAffectingAuras(this.entity);
        affectingAuras.forEach(auraInstance -> {
            BasicAura<Entity, Entity> basicAura = auraInstance.getAura();
            if (basicAura.primaryFilter.test(entity, auraInstance.getOwner()) || (WhereMagicHappens.DEVELOPER_MODE && entity instanceof ServerPlayer) && basicAura.secondaryFilter.test(entity, auraInstance.getOwner())) {
                basicAura.onTick.accept(entity, auraInstance.getOwner());
            }
        });
        updateTracking();
    }
    private final Map<SpellCaster, AuraInstance> aurasFromItems = Maps.newHashMap();
    public Map<SpellCaster, AuraInstance> getAurasFromItems() {
        return aurasFromItems;
    }
    public void onCurioEquip(ItemStack stack) {
        if (stack.getItem() instanceof SpellCaster caster && caster.getBasicAura() != null && !aurasFromItems.containsKey(caster)) {
            if (entity instanceof Player player && WhereMagicHappens.DEVELOPER_MODE && Minecraft.getInstance().getConnection() != null) player.sendSystemMessage(Component.translatable("added aura"));
            aurasFromItems.put(caster, new AuraInstance(entity, caster.getBasicAura(), entity.level()));
        }
    }
    public void onCurioUnequip(ItemStack stack) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (stack.getItem() instanceof SpellCaster caster && caster.getBasicAura() != null && aurasFromItems.containsKey(caster) && WhereMagicHappens.Abilities.getDuplicatingCurios(livingEntity, caster) <= 1) {
            if (entity instanceof Player player && WhereMagicHappens.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("removed aura"));
            aurasFromItems.get(caster).discard();
            aurasFromItems.remove(caster);
        }
    }
    public void onRespawn() {
        currDurationDisarm = 0;
        currDurationFreeze = 0;
        ancientFlameTicks = 0;
        updateTracking();
    }
    public void onEffectRecieve(MobEffectInstance effectInstance) {
        MobEffect effect = effectInstance.getEffect();
        if (effect == MobEffectRegistry.FROZEN.get()) {
            maxDurationFreeze = effectInstance.getDuration();
            currDurationFreeze = effectInstance.getDuration();
        } else if (effect == MobEffectRegistry.DISARM.get()) {
            maxDurationDisarm = effectInstance.getDuration();
            currDurationDisarm = effectInstance.getDuration();
        } else if (effect == MobEffectRegistry.DEAFENING.get()) {
            setDeafening(true);
        }
        updateTracking();
    }
    public void onEffectEnd(@Nullable MobEffectInstance effectInstance) {
        if (effectInstance == null) return;
        MobEffect effect = effectInstance.getEffect();
        if (effect == MobEffectRegistry.FROZEN.get()) {
            maxDurationFreeze = 1;
            currDurationFreeze = 0;
        } else if (effect == MobEffectRegistry.DISARM.get()) {
            maxDurationDisarm = 1;
            currDurationDisarm = 0;
        } else if (effect == MobEffectRegistry.DEAFENING.get()) {
            setDeafening(false);
        }

        updateTracking();
    }
    public void onEffectTick(MobEffectInstance effectInstance) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        MobEffect effect = effectInstance.getEffect();
        int dur = effectInstance.getDuration();
        if (effect == MobEffectRegistry.FROZEN.get()) currDurationFreeze = dur;
        if (effect == MobEffectRegistry.DISARM.get()) currDurationDisarm = dur;
        if (effect == MobEffectRegistry.DOOMED.get()) {
            soulCD--;
            whisperCD--;
            if (soulCD <= 0) {
                soulCD = WhereMagicHappens.random.nextInt(6,8);
                int num = WhereMagicHappens.random.nextInt(0, ParticleRegistry.doomedSoulParticles.size());
                WhereMagicHappens.Abilities.randomParticleOnModel(livingEntity, ParticleRegistry.doomedSoulParticles.get(num).get(),
                        WhereMagicHappens.Abilities.ParticleMovement.CHASE_AND_FALL);
            }
            if (whisperCD <= 0) {
                whisperCD = WhereMagicHappens.random.nextInt(85,125);
                if (entity instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.WHISPER.get()), serverPlayer);
                }
            }
        }
        if (effect == MobEffectRegistry.HAEMORRHAGE.get()) {
            if (WhereMagicHappens.random.nextFloat() >= 0.85f) {
                int num = WhereMagicHappens.random.nextInt(0, ParticleRegistry.bloodParticles.size());
                WhereMagicHappens.Abilities.randomParticleOnModel(livingEntity, ParticleRegistry.bloodParticles.get(num).get(), WhereMagicHappens.Abilities.ParticleMovement.FALL);
            }
        }
        updateTracking();
    }
    public void onHit(LivingHurtEvent event) {
        damageTicks = 100;
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        WhereMagicHappens.Abilities.getAffectingAuras(entity).forEach(auraInstance -> auraInstance.getAura().damageMultipliers.forEach((damageType, afloat) -> {
            if (source.is(damageType)) event.setAmount(amount * afloat);
        }));
    }
    public void onHit(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        WhereMagicHappens.Abilities.getAffectingAuras(entity).forEach(auraInstance -> auraInstance.getAura().damageImmunities.forEach(damageType -> {
            if (source.is(damageType)) event.setCanceled(true);
        }));
    }
    public void onHeal(float amount) {
        if (entity instanceof LivingEntity livingEntity) {
            bleedingHeal -= amount;
            if (bleedingHeal <= 0) {
                bleedingHeal = 0;
                EffectCleansing.tryCleanse(livingEntity, Cleansing.ABSOLUTE, MobEffectRegistry.HAEMORRHAGE.get());
            }
        }
    }
    // common
    public int tick = 0;
    public int jumpingTick = 0;
    public boolean jumpUp = true;
    public int ancientFlameTicks = 0;
    public int damageTicks = 0;
    public float bleedingHeal = 0;
    public int soulCD = 0;
    public int whisperCD = 0;
    public void setAncientFlameTicks(int ticks) {
        this.ancientFlameTicks = ticks;
        updateTracking();
    }
    public void ancientBurn(int ticks) {
        if (ticks > ancientFlameTicks) ancientFlameTicks = ticks;
        updateTracking();
    }
    public boolean isAncientBurning() {
        return ancientFlameTicks > 0 || entity.getFeetBlockState().is(BlockRegistry.ANCIENT_FLAME.get());
    }
    // bleeding sound cd
    private int bleedingSoundCD = 0;
    public void onBleedSound() {
        bleedingSoundCD = 10;
        updateTracking();
    }
    public void setBleedingHeal(float bleedingHeal) {
        this.bleedingHeal = bleedingHeal;
    }
    public boolean makeBleedSound() {
        return bleedingSoundCD <= 0;
    }
    // freeze effect data
    private int maxDurationFreeze = 1;
    private int currDurationFreeze = 0;
    public float freezeEffectPercent() {
        return (float) currDurationFreeze / maxDurationFreeze;
    }
    public float freezePercent() {
        if (entity == null) return 0f;
        return entity.getPercentFrozen();
    }
    public boolean renderFreeze() {
        return freezePercent() > 0 || freezeEffectPercent() > 0;
    }

    // disarm effect data
    private int maxDurationDisarm = 1;
    private int currDurationDisarm = 0;
    public boolean renderDisarm() {
        return currDurationDisarm > 0;
    }
    // deafening data
    private boolean hasDeafening = false;
    public void setDeafening(boolean flag) {
        hasDeafening = flag;
    }
    public boolean isDeaf() {
        return hasDeafening;
    }
    // fury data
    private boolean furious = false;
    public void setFurious(boolean furious) {
        this.furious = furious;
        updateTracking();
    }
    public boolean isFurious() {
        return furious;
    }
}
