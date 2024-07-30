package net.arkadiyhimself.fantazia.advanced.healing;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAuras;
import net.arkadiyhimself.fantazia.advanced.capacity.spellhandler.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.capacity.spellhandler.Spells;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AdvancedHealing {
    public static boolean heal(LivingEntity entity, HealingSource source, float amount) {
        float j = FTZEvents.ForgeExtenstion.onAdvancedHealing(entity, source, amount);
        if (j <= 0) return false;
        if (entity.getHealth() == entity.getMaxHealth()) return false;
        List<HealingTag> tags = source.getType().getTags();
        if (cancelHeal(entity) && !tags.contains(HealingTag.CANNOT_BE_CANCELLED)) return false;
        if (FantazicCombat.isInvulnerable(entity) && !tags.contains(HealingTag.BYPASSES_INVULNERABILITY)) return false;
        if (AuraHelper.affected(entity, BasicAuras.DESPAIR) && !tags.contains(HealingTag.UNHOLY)) j *= 0.5f;
        entity.setHealth(entity.getHealth() + j);
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(source.getType().getExhaustion());
        }
        if (source.noParticles()) return true;
        List<SimpleParticleType> types = source.getType().getParticleTypes();
        List<RegistryObject<SimpleParticleType>> regTypes = source.getType().getRegParticleTypes();
        SimpleParticleType custom = source.getCustomParticle();
        if (custom != null) {
            float num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 2 * j;
                case DECREASED -> 4 * j;
                case ALL -> 6 * j;
            };
            for (int i = 0; i <= num; i++) VisualHelper.randomParticleOnModel(entity, custom, VisualHelper.ParticleMovement.ASCEND);
        } else if (!regTypes.isEmpty()) {
              float num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> Math.min(4, j * 4);
                case DECREASED -> Math.min(6, j * 6);
                case ALL -> Math.min(8, j * 8);
            };
            for (int i = 0; i <= num; i++) {
                int l = Fantazia.RANDOM.nextInt(0, regTypes.size());
                VisualHelper.randomParticleOnModel(entity, regTypes.get(l).get(), VisualHelper.ParticleMovement.REGULAR);
            }
        } else if (!types.isEmpty()) {
            float num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 2 * j;
                case DECREASED -> 4 * j;
                case ALL -> 6 * j;
            };
            for (int i = 0; i <= num; i++) {
                int l = Fantazia.RANDOM.nextInt(0, types.size());
                VisualHelper.randomParticleOnModel(entity, types.get(l), VisualHelper.ParticleMovement.REGULAR);
            }
        }
        return true;
    }
    private static boolean cancelHeal(LivingEntity entity) {
        if (entity.hasEffect(FTZMobEffects.FROZEN) || entity.hasEffect(FTZMobEffects.DOOMED)) return true;
        if (SpellHelper.hasSpell(entity, Spells.ENTANGLE)) return true;
        return false;
    }
}
