package net.arkadiyhimself.fantazia.common.advanced.healing;

import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.common.FantazicHooks;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.common.advanced.cleanse.EffectCleansing;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.custom.Auras;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZHealingTypeTags;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

public class AdvancedHealing {

    private AdvancedHealing() {}

    public static boolean tryHeal(LivingEntity entity, HealingSource source, float amount) {
        if (entity.isDeadOrDying()) return false;
        float j = FantazicHooks.ForgeExtension.onAdvancedHealing(entity, source, amount);
        if (!canHeal(entity, source)) return false;
        if (AuraHelper.affected(entity, Auras.DESPAIR.get()) && !source.is(FTZHealingTypeTags.UNHOLY)) j *= 0.5f;
        entity.setHealth(entity.getHealth() + j);
        if (entity instanceof Player player) player.causeFoodExhaustion(source.type().exhaustion());
        healHaemorrhage(entity, amount);

        if (source.noParticles()) return true;
        RandomList<SimpleParticleType> particleTypes = source.particleTypes();
        if (particleTypes.isEmpty()) return true;

        if (source.is(FTZHealingTypeTags.REGEN)) {
            if (RandomUtil.nextFloat() < 0.15) VisualHelper.particleOnEntityServer(entity, particleTypes.random(), ParticleMovement.REGULAR);
            return true;
        }

        int num = (int) (2 * j);

        VisualHelper.particleOnEntityServer(entity, particleTypes.random(), ParticleMovement.REGULAR, num);
        return true;
    }

    private static void healHaemorrhage(LivingEntity entity, float amount) {
        float bleedingHealth = entity.getData(FTZAttachmentTypes.HAEMORRHAGE_TO_HEAL);
        float newHealth = Math.max(bleedingHealth - amount, 0);
        if (newHealth == 0) EffectCleansing.forceCleanse(entity, FTZMobEffects.HAEMORRHAGE);
        entity.setData(FTZAttachmentTypes.HAEMORRHAGE_TO_HEAL, newHealth);
    }

    private static boolean canHeal(LivingEntity entity, HealingSource source) {
        return !(entity instanceof ArmorStand) && entity.getHealth() < entity.getMaxHealth() && (!cancelHeal(entity) || source.is(FTZHealingTypeTags.NOT_CANCELLABLE)) && (!FantazicCombat.isInvulnerable(entity) || source.is(FTZHealingTypeTags.BYPASSES_INVULNERABILITY));
    }

    private static boolean cancelHeal(LivingEntity entity) {
        if (entity.hasEffect(FTZMobEffects.FROZEN) || entity.hasEffect(FTZMobEffects.DOOMED)) return true;
        return SpellHelper.spellAvailable(entity, Spells.ENTANGLE);
    }
}
