package net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager;

import net.arkadiyhimself.fantazia.AdvancedMechanics.Auras.BasicAuras;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.NewEvents;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.api.ItemRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AdvancedHealing {
    public static boolean heal(LivingEntity entity, HealingSource source, float amount) {
        float j = NewEvents.ForgeExtenstion.onAdvancedHealing(entity, source, amount);
        if (j <= 0) return false;
        if (entity.getHealth() == entity.getMaxHealth()) return false;
        List<HealingTag> tags = source.getType().getTags();
        if (checkForHealCancellations(entity) && !tags.contains(HealingTag.CANNOT_BE_CANCELLED)) return false;
        if (WhereMagicHappens.Abilities.isInvulnerable(entity) && !tags.contains(HealingTag.BYPASSES_INVULNERABILITY)) return false;
        if (WhereMagicHappens.Abilities.isUnderAura(entity, BasicAuras.DESPAIR) && !tags.contains(HealingTag.UNHOLY)) j *= 0.5f;
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
            for (int i = 0; i <= num; i++) {
                WhereMagicHappens.Abilities.randomParticleOnModel(entity, custom, WhereMagicHappens.Abilities.ParticleMovement.ASCEND);
            }
        } else if (!regTypes.isEmpty()) {
              float num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> Math.min(4, j * 4);
                case DECREASED -> Math.min(6, j * 6);
                case ALL -> Math.min(8, j * 8);
            };
            for (int i = 0; i <= num; i++) {
                int l = WhereMagicHappens.random.nextInt(0, regTypes.size());
                WhereMagicHappens.Abilities.randomParticleOnModel(entity, regTypes.get(l).get(), WhereMagicHappens.Abilities.ParticleMovement.REGULAR);
            }
        } else if (!types.isEmpty()) {
            float num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 2 * j;
                case DECREASED -> 4 * j;
                case ALL -> 6 * j;
            };
            for (int i = 0; i <= num; i++) {
                int l = WhereMagicHappens.random.nextInt(0, types.size());
                WhereMagicHappens.Abilities.randomParticleOnModel(entity, types.get(l), WhereMagicHappens.Abilities.ParticleMovement.REGULAR);
            }
        }
        float finalJ = j;
        AttachCommonData.get(entity).ifPresent(commonData -> commonData.onHeal(finalJ));
        return true;
    }
    private static boolean checkForHealCancellations(LivingEntity entity) {
        if (entity.hasEffect(MobEffectRegistry.FROZEN.get()) || entity.hasEffect(MobEffectRegistry.DOOMED.get())) return true;
        if (WhereMagicHappens.Abilities.hasCurio(entity, ItemRegistry.ENTANGLER.get())) return true;
        return false;
    }

}
