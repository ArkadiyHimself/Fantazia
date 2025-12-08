package net.arkadiyhimself.fantazia.common.api.attachment;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.tags.FTZBlockTags;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class BasicAttachmentsUtil {

    public static void maybeCrushNearbyEnemies(Player player, double fallingSpeed) {
        if (!player.getData(FTZAttachmentTypes.HURT_BY_FALLING_PERK) || fallingSpeed >= -0.6) return;
        Vec3 pos = player.position();

        if (player.level() instanceof ClientLevel clientLevel) {
            IPacket.maybeCrushNearbyEnemies(fallingSpeed);
            if (fallingSpeed < -1.2)
                clientLevel.playLocalSound(pos.x, pos.y, pos.z, FTZSoundEvents.ENTITY_CRUSH_ON.value(), SoundSource.PLAYERS, 1f, 1f,false);
            BlockState below = player.getBlockStateOn();
            for (int i = 0; i <= Math.abs(fallingSpeed * 250); i++) {
                BlockParticleOption option = new BlockParticleOption(FTZParticleTypes.CRUSH_ON.value(), below).setPos(player.blockPosition());
                Vec3 delta = RandomUtil.randomHorizontalVec3().add(0,RandomUtil.nextDouble() * 0.5,0).normalize().scale(RandomUtil.nextDouble());
                Vec3 particlePos = pos.add(delta);

                Vec3 velocity = delta.scale(Math.min(1f, 0.35f * Math.abs(fallingSpeed)) / (delta.length() * delta.length()));
                clientLevel.addParticle(option, particlePos.x, particlePos.y, particlePos.z, velocity.x, velocity.y, velocity.z);
            }
        } else {
            if (fallingSpeed < -1.2)
                player.level().playSound(player, player.blockPosition(), FTZSoundEvents.ENTITY_CRUSH_ON.value(), SoundSource.PLAYERS);
            float hurtDamage = (float) fallingSpeed * -8.75f;
            double radius = Math.min(8, Math.abs(fallingSpeed) * 6.5);
            Fantazia.LOGGER.info("Crush on damage: {}", hurtDamage);
            Fantazia.LOGGER.info("Falling speed: {}", fallingSpeed);
            Fantazia.LOGGER.info("Radius: {}", radius);
            AABB box = player.getBoundingBox().inflate(radius, 0, radius);
            List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, box,
                    living -> living != player && living.isAlive() && living.distanceTo(player) <= radius);

            for (LivingEntity entity : targets) {
                double dist = entity.distanceTo(player);
                LevelAttributesHelper.hurtEntity(entity, player, hurtDamage / (float) (dist + 0.5f), DamageSourcesHolder::crushDown);
            }
        }
    }

    public static void maybeDoEnhancedMining(Player player, BlockPos pos, boolean X) {
        List<BlockPos> blockPosList = Lists.newArrayList();
        for (int i = -1; i <= 1; i++) // x
            for (int j = -1; j <= 1; j++)
                for (int k = -1; k <= 1; k++) // z
                {
                    if (i != 0 && X || k != 0 && !X) continue;
                    BlockPos blockPos = new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
                    if (!blockPos.equals(pos)) blockPosList.add(blockPos);
                }

        for (BlockPos blockPos : blockPosList) {
            BlockState blockState = player.level().getBlockState(blockPos);
            if (!blockState.is(FTZBlockTags.IGNORED_BY_ENHANCED_MINING_PERK))
                FantazicUtil.destroyBlock(blockPos, blockState, player);
        }
    }

    public static void maybeApplyMinersSight(Player player) {
        if (player.getData(FTZAttachmentTypes.MINERS_SIGHT_PERK) && !player.level().isClientSide()) {
            Vec3 eye = player.getEyePosition();
            BlockPos eyePos = new BlockPos((int) eye.x, (int) eye.y, (int) eye.z);
            int light = player.level().getBrightness(LightLayer.SKY, eyePos);
            if (light < 2) player.addEffect(
                    new MobEffectInstance(MobEffects.NIGHT_VISION,205,0, false,false,true)
            );
        }
    }

    public static void maybeApplyFerocious(DamageSource source, LivingEntity target) {
        Entity entity = source.getEntity();
        if (entity == null || !entity.getData(FTZAttachmentTypes.UPPER_HAND_PERK) || !source.is(FTZDamageTypeTags.USED_BY_FEROCIOUS_PERK)) return;
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,3));
        target.addEffect(new MobEffectInstance(FTZMobEffects.CORROSION,100,5));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,100,0));
    }

    public static void maybeApplyPinpoint(DamageSource source, Entity entity, float amount) {
        Entity owner = source.getEntity();
        DamageSourcesHolder holder = LevelAttributesHelper.getDamageSources(entity.level());
        if (!source.is(FTZDamageTypeTags.USED_BY_PINPOINT_PERK) || holder == null || owner == null || !owner.getData(FTZAttachmentTypes.PINPOINT_PERK) || entity.level().isClientSide()) return;
        double distance = owner.distanceTo(entity);
        if (distance < 5) return;
        DamageSource pinpoint = holder.pinpoint(owner);
        Vec3 pos = entity.position();
        AABB aabb = AABB.ofSize(pos,6,6,6);
        List<? extends Entity> entities = entity.level().getEntities(entity.getType(), aabb, entity1 -> entity1 != entity && entity1 != owner);
        for (Entity entity1 : entities) {
            entity1.hurt(pinpoint, amount * 0.2f);
        }
    }
}
