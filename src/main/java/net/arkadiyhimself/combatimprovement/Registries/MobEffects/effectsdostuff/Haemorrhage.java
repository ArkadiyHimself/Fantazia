package net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.Registries.Particless.ParticleRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.DataSync;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Haemorrhage extends MobEffect {
    public int passiveDMGdelay = 50;
    public int activeDMGdelay = 10;
    public static DamageSource BLEEDING = new DamageSource("bleeding").bypassArmor().bypassEnchantments().bypassMagic();

    public Haemorrhage(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        passiveDMGdelay = Math.max(0, passiveDMGdelay - 1);
        activeDMGdelay = Math.max(0, activeDMGdelay - 1);
        return true;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        float damage;
        Vec3 vector;
        if (pLivingEntity instanceof ServerPlayer player) {
            Dash dash = AttachDash.getUnwrap(player);
            if (dash != null && dash.isDashing()) {
                player.hurt(BLEEDING, 6.5F);
                return;
            }
            DataSync dataSync = AttachDataSync.getUnwrap(player);
            if (dataSync == null) { return; }
            vector = dataSync.getDeltaMovement();
        } else {
            vector = pLivingEntity.getDeltaMovement();
        }
        double velocity = Math.sqrt(vector.x() * vector.x() + vector.z() * vector.z());

        if (velocity == 0 || pLivingEntity.isCrouching()) {
            if (passiveDMGdelay == 0) {
                damage = 0.5F;
                pLivingEntity.hurt(BLEEDING, damage);
                passiveDMGdelay = Math.max(40, 80 - pAmplifier * 5);
                int num = switch (Minecraft.getInstance().options.particles().get()) {
                    case MINIMAL -> 1;
                    case DECREASED -> 2;
                    case ALL -> 3;
                };
                int amount = (int) Math.max(10, num * damage * 0.1);
                for (int i = 1; i <= amount; i++) {
                    Random random = new Random();

                    UsefulMethods.Abilities.createRandomParticleOnHumanoid(pLivingEntity,
                            ParticleRegistry.bloodParticles.get(random.nextInt(0, ParticleRegistry.bloodParticles.size())).get(),
                            UsefulMethods.Abilities.ParticleMovement.FALL);
                }
            }
        }  else if (velocity > 0 && activeDMGdelay == 0) {
            damage = (float) ((pAmplifier * 0.25F + 1) * ((pLivingEntity.isSprinting() && pLivingEntity.isOnGround()) ? velocity * 12.5 : velocity * 5));
            pLivingEntity.hurt(BLEEDING, damage);
            activeDMGdelay = 10;
            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 2;
                case DECREASED -> 3;
                case ALL -> 4;
            };
            int amount = (int) Math.max(15, num * damage * 0.25);
            for (int i = 1; i <= amount; i++) {
                Random random = new Random();

                UsefulMethods.Abilities.createRandomParticleOnHumanoid(pLivingEntity,
                        ParticleRegistry.bloodParticles.get(random.nextInt(0, ParticleRegistry.bloodParticles.size())).get(),
                        UsefulMethods.Abilities.ParticleMovement.FALL);
            }
            passiveDMGdelay = Math.max(25, 50 - pAmplifier * 5);
        }
    }
}
