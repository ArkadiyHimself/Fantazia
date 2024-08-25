package net.arkadiyhimself.fantazia.advanced.healing;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZHealingTypeTags;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

public class AdvancedHealing {
    public static boolean heal(LivingEntity entity, HealingSource source, float amount) {
        float j = FTZEvents.ForgeExtension.onAdvancedHealing(entity, source, amount);
        if (j <= 0) return false;
        if (entity instanceof ArmorStand) return false;
        if (entity.getHealth() == entity.getMaxHealth()) return false;
        if (cancelHeal(entity) && !source.is(FTZHealingTypeTags.NOT_CANCELLABLE)) return false;
        if (FantazicCombat.isInvulnerable(entity) && !source.is(FTZHealingTypeTags.BYPASSES_INVULNERABILITY)) return false;
        if (AuraHelper.affected(entity, FTZAuras.DESPAIR.get()) && !source.is(FTZHealingTypeTags.UNHOLY)) j *= 0.5f;
        entity.setHealth(entity.getHealth() + j);
        if (entity instanceof Player player) player.causeFoodExhaustion(source.type().exhaustion());
        if (source.noParticles()) return true;
        RandomList<SimpleParticleType> particleTypes = source.particleTypes();
        if (particleTypes.isEmpty()) return true;

        if (source.is(FTZHealingTypeTags.REGEN)) {
            if (Fantazia.RANDOM.nextFloat() < 0.15) VisualHelper.randomParticleOnModel(entity, particleTypes.random(), VisualHelper.ParticleMovement.REGULAR);
            return true;
        }
        int num = (int) switch (Minecraft.getInstance().options.particles().get()) {
            case MINIMAL -> j;
            case DECREASED -> 2 * j;
            case ALL -> 3 * j;
        };

        for (int i = 0; i <= num; i++) VisualHelper.randomParticleOnModel(entity, particleTypes.random(), VisualHelper.ParticleMovement.REGULAR);
        return true;
    }
    private static boolean cancelHeal(LivingEntity entity) {
        if (entity.hasEffect(FTZMobEffects.FROZEN.get()) || entity.hasEffect(FTZMobEffects.DOOMED.get())) return true;
        if (SpellHelper.hasSpell(entity, FTZSpells.ENTANGLE.get())) return true;
        return false;
    }
}
