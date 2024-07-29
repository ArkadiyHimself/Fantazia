package net.arkadiyhimself.fantazia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.FTZGui;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class VisualHelper {
    public enum ParticleMovement {
        REGULAR, CHASE, FALL, ASCEND, CHASE_AND_FALL, CHASE_OPPOSITE, CHASE_AND_FALL_OPPOSITE, FROM_CENTER, TO_CENTER
    }
    public static void randomParticleOnModel(Entity entity, @Nullable SimpleParticleType particle, ParticleMovement type) {
        if (particle == null) { return; }
        // getting entity's height and width
        float radius = entity.getBbWidth() * (float) 0.7;
        float height = entity.getBbHeight();

        double dx = entity.getDeltaMovement().x();
        double dy = entity.getDeltaMovement().y();
        double dz = entity.getDeltaMovement().z();

        // here im using circular function for X and Z (X**2 + Z**2 = R**2) coordinates to make a horizontal circle
        // Y variants are just a vertical line
        double y = Fantazia.RANDOM.nextDouble(0, height * 0.8);
        double x = Fantazia.RANDOM.nextDouble(-radius, radius);
        double z = java.lang.Math.sqrt(radius * radius - x * x);

        // here game randomly decides to make Z coordinate negative
        boolean negativeZ = Fantazia.RANDOM.nextBoolean();
        z = negativeZ ? z * (-1) : z;
        if (Minecraft.getInstance().level != null) {
            switch (type) {
                case REGULAR -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        0, 0, 0);
                case CHASE -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        dx * 1.5, dy * 0.2 + 0.1, dz * 1.5);
                case FALL -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        0, -0.15, 0);
                case ASCEND -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        0,0.15,0);
                case CHASE_AND_FALL -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        dx * 1.5, 00.15, dz * 1.5);
                case CHASE_OPPOSITE -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        -dx * 1.5, -(dy * 0.2 + 0.1), -dz * 1.5);
                case CHASE_AND_FALL_OPPOSITE -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        -dx * 1.5, -0.15, -dz * 1.5);
                case FROM_CENTER -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        x * 1.5, y * 1.5, z * 1.5);
                case TO_CENTER -> Minecraft.getInstance().level.addParticle(particle, true,
                        entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                        -x * 1.5, -y * 1.5, -z * 1.5);
            }
        }
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

            FTZGui.fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            FTZGui.fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            FTZGui.fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            FTZGui.fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }
        poseStack.popPose();
    }
}
