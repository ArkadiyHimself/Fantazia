package net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.Items.MagicCasters.SpellCaster;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.RenderingValues;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SpellHelper {
    public static final HashMap<ResourceLocation, SelfSpell> SELF_SPELLS = Maps.newHashMap();
    public static final HashMap<ResourceLocation, TargetedSpell<? extends LivingEntity>> TARGETED_SPELLS = Maps.newHashMap();
    public static final HashMap<ResourceLocation, PassiveSpell> PASSIVE_SPELLS = Maps.newHashMap();
    public static boolean trySelfSpell(LivingEntity entity, SelfSpell spell) {
        boolean flag = spell.conditions(entity);
        if (flag) {
            spell.onCast(entity);
            entity.level().playSound(null, entity.blockPosition(), SoundRegistry.ENTANGLE.get(), SoundSource.NEUTRAL);
        }
        return flag;
    }
    private static <T extends LivingEntity> @Nullable T getTarget(LivingEntity caster, TargetedSpell<T> spell) {
        List<LivingEntity> targets = WhereMagicHappens.Abilities.getTargets(caster, 1f, spell.getRange(), spell.thruWalls());
        targets.removeIf(livingEntity -> !spell.canAffect(livingEntity));
        List<T> newTargets = (List<T>) targets;
        newTargets.removeIf(living -> !spell.conditions(caster, living));
        if (newTargets.isEmpty()) return null;
        return (T) WhereMagicHappens.Abilities.getClosestEntity((List<LivingEntity>) newTargets, caster);
    }
    public static <T extends LivingEntity> boolean tryTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null) return false;
        spell.before(caster, target);
        boolean blocked = false;
        if (spell.canBlock()) {
            TargetedResult result = checkForAbilityBlocking(target);
            blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
            if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) {
                spell.after(caster, target);
                return false;
            }
        }
        if (!blocked) {
            spell.after(caster, target);
            return true;
        }
        return false;
    }
    @Nullable
    public static <T extends LivingEntity> T commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null) return null;
        spell.before(caster, target);
        boolean blocked = false;
        if (spell.canBlock()) {
            TargetedResult result = checkForAbilityBlocking(target);
            blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
            if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) {
                spell.after(caster, target);
            }
        }
        if (!blocked) {
            spell.after(caster, target);
        }
        return target;
    }
    public static <T extends LivingEntity> List<T> commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell, Collection<LivingEntity> entities) {
        entities.removeIf(livingEntity -> !spell.canAffect(livingEntity) || livingEntity == caster);
        List<T> newTargets = (List<T>) entities;
        newTargets.forEach(entity -> {
            spell.before(caster, entity);
            boolean blocked = false;
            if (spell.canBlock()) {
                TargetedResult result = checkForAbilityBlocking(entity);
                blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
                if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) {
                    spell.after(caster, entity);
                }
            }
            if (!blocked) {
                spell.after(caster, entity);
            }
        });
        return newTargets;
    }
    public static TargetedResult checkForAbilityBlocking(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            List<SlotResult> slotResults = WhereMagicHappens.Abilities.findAllCurio(serverPlayer, "passivecaster");
            for (SlotResult slotResult : slotResults) {
                Item item = slotResult.stack().getItem();
                if (item instanceof SpellCaster spellCaster && spellCaster.getSpell() == Spells.REFLECT && !serverPlayer.getCooldowns().isOnCooldown(spellCaster)) {
                    serverPlayer.getCooldowns().addCooldown(spellCaster, spellCaster.getSpell().getRecharge());
                    serverPlayer.level().playSound(null, serverPlayer.blockPosition(), spellCaster.getSpell().getCastSound(), SoundSource.PLAYERS);
                    AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
                    abilityManager.getAbility(RenderingValues.class).ifPresent(RenderingValues::onMirrorActivation);
                    return TargetedResult.REFLECTED;
                }
            }
        }
        int num = switch (Minecraft.getInstance().options.particles().get()) {
            case MINIMAL -> 20;
            case DECREASED -> 30;
            case ALL -> 40;
        };
        if (target.hasEffect(MobEffectRegistry.REFLECT.get())) {
            for (int i = 0; i < num; i++) {
                WhereMagicHappens.Abilities.randomParticleOnModel(target, ParticleTypes.ENCHANT, WhereMagicHappens.Abilities.ParticleMovement.FROM_CENTER);
            }
            target.removeEffect(MobEffectRegistry.REFLECT.get());
            target.level().playSound(null, target.blockPosition(), SoundRegistry.REFLECT.get(), SoundSource.PLAYERS);
            Minecraft.getInstance().level.playSound(null, target.blockPosition(), SoundRegistry.REFLECT.get(), SoundSource.PLAYERS);
            if (target instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.REFLECT.get()), serverPlayer);
            }
            return TargetedResult.REFLECTED;
        }
        if (target.hasEffect(MobEffectRegistry.DEFLECT.get())) {
            for (int i = 0; i < num; i++) {
                WhereMagicHappens.Abilities.randomParticleOnModel(target, ParticleTypes.ENCHANT, WhereMagicHappens.Abilities.ParticleMovement.FROM_CENTER);
            }
            target.removeEffect(MobEffectRegistry.DEFLECT.get());
            target.level().playSound(null, target.blockPosition(), SoundRegistry.DEFLECT.get(), SoundSource.PLAYERS);
            Minecraft.getInstance().level.playSound(null, target.blockPosition(), SoundRegistry.REFLECT.get(), SoundSource.PLAYERS);
            if (target instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DEFLECT.get()), serverPlayer);
            }
            return TargetedResult.BLOCKED;
        }
        return TargetedResult.DEFAULT;
    }
    public enum TargetedResult {
        DEFAULT, REFLECTED, BLOCKED;
    }
}
