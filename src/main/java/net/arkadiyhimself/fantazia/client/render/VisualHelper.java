package net.arkadiyhimself.fantazia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.AddParticleS2C;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class VisualHelper {
    public enum ParticleMovement {
        REGULAR(0,0,0),
        CHASE(dx -> dx * 1.5, dy -> dy * 0.2 + 0.1, dz -> dz * 1.5),
        FALL(0,-0.15,0),
        ASCEND(0,-0.15,0),
        CHASE_AND_FALL(dx -> dx * 1.5, dy -> 0.15, dz -> dz * 1.5),
        CHASE_OPPOSITE(dx -> dx * -1.5, dy -> dy * -0.2 - 0.1, dz -> dz * -1.5),
        CHASE_AND_FALL_OPPOSITE(dx -> dx * -1.5, dy -> 0.15, dz -> dz * -1.5),
        FROM_CENTER(dx -> dx * 1.5, dy -> dy * 1.5, dz -> dz * 1.5),
        TO_CENTER(dx -> dx * -1.5, dy -> dy * -1.5, dz -> dz * -1.5);
        private final Function<Double, Double> xSpeed;
        private final Function<Double, Double> ySpeed;
        private final Function<Double, Double> zSpeed;
        ParticleMovement(Function<Double, Double> xSpeed, Function<Double, Double> ySpeed, Function<Double, Double> zSpeed) {
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.zSpeed = zSpeed;
        }
        ParticleMovement(double dx, double dy, double dz) {
            this.xSpeed = x -> dx;
            this.ySpeed = y -> dy;
            this.zSpeed = z -> dz;
        }
        public Vec3 modify(Vec3 vec3) {
            double dx = vec3.x();
            double dy = vec3.y();
            double dz = vec3.z();
            return new Vec3(xSpeed(dx), ySpeed(dy), zSpeed(dz));
        }
        public double xSpeed(double playerX) {
            return xSpeed.apply(playerX);
        }
        public double ySpeed(double playerX) {
            return ySpeed.apply(playerX);
        }
        public double zSpeed(double playerX) {
            return zSpeed.apply(playerX);
        }
    }
    public static void randomParticleOnModel(Entity entity, @Nullable SimpleParticleType particle, ParticleMovement type) {
        if (particle == null || !(entity.level() instanceof ServerLevel serverLevel)) return;
        // getting entity's height and width
        float radius = entity.getBbWidth() * (float) 0.7;
        float height = entity.getBbHeight();

        double dx = entity.getDeltaMovement().x();
        double dy = entity.getDeltaMovement().y();
        double dz = entity.getDeltaMovement().z();

        Vec3 vec3 = new Vec3(Fantazia.RANDOM.nextDouble(-1,1), 0, Fantazia.RANDOM.nextDouble(-1,1)).normalize().scale(radius);
        double x = vec3.x();
        double z = vec3.z();
        double y = Fantazia.RANDOM.nextDouble(0, height * 0.8);

        double x0 = entity.getX() + x;
        double y0 = entity.getY() + y;
        double z0 = entity.getZ() + z;


        double DX = type.xSpeed(dx);
        double DY = type.xSpeed(dy);
        double DZ = type.xSpeed(dz);

        NetworkHandler.sendToPlayers(new AddParticleS2C(new Vec3(x0, y0, z0), new Vec3(DX, DY, DZ), particle), serverLevel);
    }
    public static <T extends ParticleOptions> void rayOfParticles(LivingEntity caster, LivingEntity target, T type) {
        Vec3 vec3 = caster.position().add(0.0D, 1.2F, 0.0D);
        Vec3 vec31 = target.getEyePosition().subtract(vec3);
        Vec3 vec32 = vec31.normalize();

        for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
            Vec3 vec33 = vec3.add(vec32.scale(i));
            if (caster.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(type, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    public static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void renderAncientFlame(PoseStack poseStack, LivingEntity entity, MultiBufferSource buffers) {
        poseStack.pushPose();
        TextureAtlasSprite textureatlassprite0 = FTZGuis.ANCIENT_FLAME_0.sprite();
        TextureAtlasSprite textureatlassprite1 = FTZGuis.ANCIENT_FLAME_1.sprite();
        float f = entity.getBbWidth() * 1.4F;
        poseStack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = entity.getBbHeight() / f;
        float f4 = 0.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot()));
        poseStack.translate(0.0F, 0.0F, -0.3F + (float)((int)f3) * 0.02F);
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
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderBlinkingEntity(T entity, LivingEntityRenderer<T,M> renderer, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        float scale = Fantazia.RANDOM.nextFloat(-0.75F,0.75F);
        Vec3 vec3 = new Vec3(Fantazia.RANDOM.nextDouble(-1,1), 0, Fantazia.RANDOM.nextDouble(-1,1)).normalize().scale(scale);
        poseStack.scale(-1,-1,1);
        poseStack.translate(vec3.x(), -1.501F, vec3.z());

        RenderType renderType = renderer.getModel().renderType(renderer.getTextureLocation(entity));
        VertexConsumer consumer = buffers.getBuffer(renderType);

        float i1 = Fantazia.RANDOM.nextFloat(0.15f,0.45f);
        float i2 = Fantazia.RANDOM.nextFloat(0.15f,0.45f);
        float i3 = Fantazia.RANDOM.nextFloat(0.65f,0.95f);

        float r;
        float g;
        float b;

        int j = Fantazia.RANDOM.nextInt(1,4);
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
        renderer.getModel().renderToBuffer(poseStack, consumer, packedLight, packedOverlay, r, g, b,0.65f);

        poseStack.popPose();

    }
}
