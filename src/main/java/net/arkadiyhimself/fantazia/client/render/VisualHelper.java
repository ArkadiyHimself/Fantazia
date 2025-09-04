package net.arkadiyhimself.fantazia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.LocationHolder;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.client.particless.options.EntityChasingParticleOption;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class VisualHelper {

    public static void particleOnEntityServer(Entity entity, @Nullable ParticleOptions particle, ParticleMovement movement, int amount, float range) {
        if (particle == null || entity.level().isClientSide()) return;
        IPacket.addParticlesOnEntity(entity, particle, movement, amount, range);
    }

    public static void particleOnEntityServer(Entity entity, @Nullable ParticleOptions particle, ParticleMovement movement, int amount) {
        particleOnEntityServer(entity, particle, movement, amount, 1f);
    }

    public static void particleOnEntityServer(Entity entity, @Nullable ParticleOptions particle, ParticleMovement movement) {
        particleOnEntityServer(entity, particle, movement, 1, 1f);
    }

    public static void particleOnEntityClient(Entity entity, @Nullable ParticleOptions particle, ParticleMovement type) {
        particleOnEntityClient(entity, particle, type, 1f);
    }

    public static void particleOnEntityClient(Entity entity, @Nullable ParticleOptions particle, ParticleMovement type, float range) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (particle == null || clientLevel == null) return;

        // getting entity's height and width
        float radius = entity.getBbWidth() * (float) 0.7;
        float height = entity.getBbHeight();

        // the resulting position is supposed to be on a "cylinder" around entity, not sphere, which is why y coordinate is taken separately
        Vec3 vec3 = RandomUtil.randomHorizontalVec3().normalize().scale(radius).scale(range);
        double x = vec3.x();
        double z = vec3.z();
        double y = RandomUtil.nextDouble(height * 0.1,height * 0.8);

        double x0 = entity.getX() + x;
        double y0 = entity.getY() + y;
        double z0 = entity.getZ() + z;

        Vec3 delta = type.modify(new Vec3(x0, y0, z0), entity.getDeltaMovement());

        clientLevel.addParticle(particle, x0, y0, z0, delta.x, delta.y, delta.z);
    }

    public static void entityChasingParticle(Entity entity, Supplier<EntityChasingParticleOption<?>> factory, int amount) {
        List<ParticleOptions> options = Lists.newArrayList();
        for (int i = 0; i < amount; i++) options.add(factory.get());
        if (factory != null) IPacket.addChasingParticles(entity, options);
    }

    public static void entityChasingParticle(Entity entity, ParticleType<EntityChasingParticleOption<?>> type, int amount) {
        entityChasingParticle(entity, () -> new EntityChasingParticleOption<>(entity.getId(), type, entity.getBbWidth() * 0.7f, entity.getBbHeight()), amount);
    }

    public static void entityChasingParticle(Entity entity, ParticleType<EntityChasingParticleOption<?>> type, int amount, float width) {
        entityChasingParticle(entity, () -> new EntityChasingParticleOption<>(entity.getId(), type, entity.getBbWidth() * 0.7f * width, entity.getBbHeight()), amount);
    }

    public static <T extends ParticleOptions> void rayOfParticles(LivingEntity caster, LivingEntity target, T type) {
        if (!(caster.level() instanceof ServerLevel serverLevel)) return;
        Vec3 vec3 = caster.position().add(0.0D, 1.2F, 0.0D);
        Vec3 vec31 = target.getEyePosition().subtract(vec3);
        Vec3 vec32 = vec31.normalize();

        for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
            Vec3 vec33 = vec3.add(vec32.scale(i));
            serverLevel.sendParticles(type, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
    public static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.addVertex(pMatrixEntry.pose(), pX, pY, pZ).setColor(255, 255, 255, 255).setUv(pTexU, pTexV).setUv1(0, 10).setLight(240).setNormal(pMatrixEntry.copy(), 0.0F, 1.0F, 0.0F);
    }
    public static void renderAncientFlame(PoseStack poseStack, Entity entity, MultiBufferSource buffers) {
        poseStack.pushPose();
        TextureAtlasSprite textureatlassprite0 = FTZGuis.ANCIENT_FLAME_0.sprite();
        TextureAtlasSprite textureatlassprite1 = FTZGuis.ANCIENT_FLAME_1.sprite();

        float f = entity.getBbWidth() * 1.4F;
        poseStack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = entity.getBbHeight() / f;
        float f4 = 0.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot()));
        poseStack.translate(0.0F, 0.0F, -0.3F + (f3) * 0.02F);
        float f5 = 0.0F;
        int i = 0;
        VertexConsumer vertexconsumer = buffers.getBuffer(Sheets.cutoutBlockSheet());

        for(PoseStack.Pose posestack$pose = poseStack.last(); f3 > 0.0F; ++i) {
            TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite0 : textureatlassprite1;
            float f6 = textureatlassprite2.getU0();
            float f7 = textureatlassprite2.getV0();
            float f8 = textureatlassprite2.getU1();
            float f9 = textureatlassprite2.getV1();
            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }
        poseStack.popPose();
    }
    public static void renderEvasionPlayer(AbstractClientPlayer entity, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        float scale = RandomUtil.nextFloat(-0.75F,0.75F);
        Vec3 vec3 = RandomUtil.randomHorizontalVec3().normalize().scale(scale);
        poseStack.scale(-1,-1,1);
        poseStack.translate(vec3.x(), -1.501F, vec3.z());

        RenderType renderType = renderer.getModel().renderType(renderer.getTextureLocation(entity));
        VertexConsumer consumer = buffers.getBuffer(renderType);

        int i1 = RandomUtil.nextInt(40, 120);
        int i2 = RandomUtil.nextInt(40, 120);
        int i3 = RandomUtil.nextInt(165, 240);

        int r;
        int g;
        int b;

        int j = RandomUtil.nextInt(1,4);
        if (j == 1) {
            r = i1;
            g = i2;
            b = i3;
        } else if (j == 2) {
            r = i3;
            g = i1;
            b = i2;
        } else {
            r = i2;
            g = i3;
            b = i1;
        }
        renderer.getModel().renderToBuffer(poseStack, consumer, packedLight, packedOverlay, FastColor.ARGB32.color(150, r, g, b));

        poseStack.popPose();
    }
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderEvasionEntity(T entity, LivingEntityRenderer<T,M> renderer, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        float scale = RandomUtil.nextFloat(-0.75F,0.75F);
        Vec3 vec3 = RandomUtil.randomHorizontalVec3().normalize().scale(scale);
        poseStack.scale(-1,-1,1);
        poseStack.translate(vec3.x(), -1.501F, vec3.z());

        RenderType renderType = renderer.getModel().renderType(renderer.getTextureLocation(entity));
        VertexConsumer consumer = buffers.getBuffer(renderType);

        int i1 = RandomUtil.nextInt(40,120);
        int i2 = RandomUtil.nextInt(40,120);
        int i3 = RandomUtil.nextInt(165,240);

        int r;
        int g;
        int b;

        int j = RandomUtil.nextInt(1,4);
        if (j == 1) {
            r = i1;
            g = i2;
            b = i3;
        } else if (j == 2) {
            r = i3;
            g = i1;
            b = i2;
        } else {
            r = i2;
            g = i3;
            b = i1;
        }
        renderer.getModel().renderToBuffer(poseStack, consumer, packedLight, packedOverlay, FastColor.ARGB32.color(150, r, g, b));

        poseStack.popPose();
    }

    public static void circleOfParticles(ParticleOptions particle, Vec3 pos) {
        if (Minecraft.getInstance().level == null) return;
        double d0 = pos.x;
        double d7 = pos.y;
        double d9 = pos.z;
        for(double d12 = 0.0D; d12 < (Math.PI * 2D); d12 += 0.15707963267948966D) {
            Minecraft.getInstance().level.addParticle(particle, d0 + Math.cos(d12) * 5.0D, d7 - 0.4D, d9 + Math.sin(d12) * 5.0D, Math.cos(d12) * -5.0D, 0.0D, Math.sin(d12) * -5.0D);
            Minecraft.getInstance().level.addParticle(particle, d0 + Math.cos(d12) * 5.0D, d7 - 0.4D, d9 + Math.sin(d12) * 5.0D, Math.cos(d12) * -7.0D, 0.0D, Math.sin(d12) * -7.0D);
        }
    }

    public static float layerOffset(float pTickCount) {
        return pTickCount * 0.01F;
    }

    public static void wanderersSpiritParticles() {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null || Minecraft.getInstance().isPaused()) return;
        LocationHolder locationHolder = player.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
        Vec3 pos = locationHolder.position();
        if (locationHolder.empty() || !locationHolder.isIn(level) || !level.isLoaded(BlockPos.containing(pos))) return;

        Vec3 delta = RandomUtil.randomVec3().normalize().scale(0.4);
        Vec3 finalPos = pos.add(delta).add(0,0.3,0);

        for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() + 2; i++) level.addParticle(ParticleTypes.PORTAL, finalPos.x, finalPos.y, finalPos.z, (RandomUtil.nextDouble(-1, 1) - 0.5) * 2.0, -RandomUtil.nextDouble(), (RandomUtil.nextDouble(-1, 1) - 0.5) * 2.0);
    }

    public static Component componentLevel(int level) {
        if (level >= 1 && level <= 10) return Component.translatable("enchantment.level." + level);
        else return Component.literal(String.valueOf(level));
    }
}
