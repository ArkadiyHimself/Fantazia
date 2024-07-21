package net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.Items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registry.*;
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
        tag.putInt("ancientFlameTicks", ancientFlameTicks);

        tag.putInt("damageTicks", damageTicks);
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        ancientFlameTicks = nbt.contains("ancientFlameTicks") ? nbt.getInt("ancientFlameTicks") : 0;

        damageTicks = nbt.contains("damageTicks") ? nbt.getInt("damageTicks") : 0;
    }
    public void tick() {

        if (entity.level().isClientSide()) return;
        if (damageTicks >= 0) damageTicks--;
        ancientFlameTicks = Math.max(0, ancientFlameTicks - 1);
        if (ancientFlameTicks > 0) entity.hurt(new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.ANCIENT_BURNING)), 1.5f);
        List<AuraInstance<Entity, Entity>> affectingAuras = WhereMagicHappens.Abilities.getAffectingAuras(this.entity);
        affectingAuras.forEach(auraInstance -> {
            BasicAura<Entity, Entity> basicAura = auraInstance.getAura();
            if (basicAura.primaryFilter.test(entity, auraInstance.getOwner()) || (Fantazia.DEVELOPER_MODE && entity instanceof ServerPlayer) && basicAura.secondaryFilter.test(entity, auraInstance.getOwner())) {
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
            if (entity instanceof Player player && Fantazia.DEVELOPER_MODE && Minecraft.getInstance().getConnection() != null) player.sendSystemMessage(Component.translatable("added aura"));
            aurasFromItems.put(caster, new AuraInstance(entity, caster.getBasicAura(), entity.level()));
        }
    }
    public void onCurioUnequip(ItemStack stack) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (stack.getItem() instanceof SpellCaster caster && caster.getBasicAura() != null && aurasFromItems.containsKey(caster) && WhereMagicHappens.Abilities.getDuplicatingCurios(livingEntity, caster) <= 1) {
            if (entity instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("removed aura"));
            aurasFromItems.get(caster).discard();
            aurasFromItems.remove(caster);
        }
    }
    public void onRespawn() {
        ancientFlameTicks = 0;
        updateTracking();
    }
    public void onEffectTick(MobEffectInstance effectInstance) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        MobEffect effect = effectInstance.getEffect();

        if (effect == MobEffectRegistry.HAEMORRHAGE.get()) {
            if (Fantazia.RANDOM.nextFloat() >= 0.85f) {
                int num = Fantazia.RANDOM.nextInt(0, ParticleRegistry.BLOOD.size());
                WhereMagicHappens.Abilities.randomParticleOnModel(livingEntity, ParticleRegistry.BLOOD.get(num).get(), WhereMagicHappens.Abilities.ParticleMovement.FALL);
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
        setPrevHP(event.getEntity().getHealth());
        WhereMagicHappens.Abilities.getAffectingAuras(entity).forEach(auraInstance -> auraInstance.getAura().damageImmunities.forEach(damageType -> {
            if (source.is(damageType)) event.setCanceled(true);
        }));
    }
    // common
    public int damageTicks = 0;
    private float prevHP = 10;
    public float getPrevHP() {
        return prevHP;
    }
    public void setPrevHP(float prevHP) {
        this.prevHP = prevHP;
    }

    // ancient flame
    public int ancientFlameTicks = 0;
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
}
