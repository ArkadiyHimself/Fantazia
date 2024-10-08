package net.arkadiyhimself.fantazia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.networking.packets.stuff.AddParticleS2C;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

@OnlyIn(Dist.CLIENT)
public class VisualHelper {
    public static void randomParticleOnModel(Entity entity, @Nullable SimpleParticleType particle, ParticleMovement type) {
        if (particle == null) return;
        // getting entity's height and width
        float radius = entity.getBbWidth() * (float) 0.7;
        float height = entity.getBbHeight();

        Vec3 vec3 = new Vec3(Fantazia.RANDOM.nextDouble(-1,1), 0, Fantazia.RANDOM.nextDouble(-1,1)).normalize().scale(radius);
        double x = vec3.x();
        double z = vec3.z();
        double y = Fantazia.RANDOM.nextDouble(0, height * 0.8);

        double x0 = entity.getX() + x;
        double y0 = entity.getY() + y;
        double z0 = entity.getZ() + z;

        Vec3 delta = type.modify(new Vec3(x0, y0, z0), entity.getDeltaMovement());

        PacketDistributor.sendToAllPlayers(new AddParticleS2C(new Vec3(x0, y0, z0).toVector3f(), delta.toVector3f(), particle));
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

        float scale = Fantazia.RANDOM.nextFloat(-0.75F,0.75F);
        Vec3 vec3 = new Vec3(Fantazia.RANDOM.nextDouble(-1,1), 0, Fantazia.RANDOM.nextDouble(-1,1)).normalize().scale(scale);
        poseStack.scale(-1,-1,1);
        poseStack.translate(vec3.x(), -1.501F, vec3.z());

        RenderType renderType = renderer.getModel().renderType(renderer.getTextureLocation(entity));
        VertexConsumer consumer = buffers.getBuffer(renderType);

        int i1 = Fantazia.RANDOM.nextInt(40,120);
        int i2 = Fantazia.RANDOM.nextInt(40,120);
        int i3 = Fantazia.RANDOM.nextInt(165,240);

        int r;
        int g;
        int b;

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
        renderer.getModel().renderToBuffer(poseStack, consumer, packedLight, packedOverlay, FastColor.ARGB32.color(150, r, g, b));

        poseStack.popPose();
    }
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderEvasionEntity(T entity, LivingEntityRenderer<T,M> renderer, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        float scale = Fantazia.RANDOM.nextFloat(-0.75F,0.75F);
        Vec3 vec3 = new Vec3(Fantazia.RANDOM.nextDouble(-1,1), 0, Fantazia.RANDOM.nextDouble(-1,1)).normalize().scale(scale);
        poseStack.scale(-1,-1,1);
        poseStack.translate(vec3.x(), -1.501F, vec3.z());

        RenderType renderType = renderer.getModel().renderType(renderer.getTextureLocation(entity));
        VertexConsumer consumer = buffers.getBuffer(renderType);

        int i1 = Fantazia.RANDOM.nextInt(40,120);
        int i2 = Fantazia.RANDOM.nextInt(40,120);
        int i3 = Fantazia.RANDOM.nextInt(165,240);

        int r;
        int g;
        int b;

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

    public enum ParticleMovement {
        REGULAR(new Vec3(0,0,0)),
        CHASE((pos, delta) -> new Vec3(delta.x() * 1.5, delta.y() * 0.2 + 0.1, delta.z() * 1.5)),
        FALL(new Vec3(0,-0.15,0)),
        ASCEND(new Vec3(0,0.15,0)),
        CHASE_AND_FALL((pos, delta) -> new Vec3(delta.x() * 1.5, 0.15, delta.z() * 1.5)),
        AWAY((pos, delta) -> new Vec3(delta.x() *(-1.5), delta.y() * -0.2 - 0.1, delta.z() *(-1.5))),
        AWAY_AND_FALL((pos, delta) -> new Vec3(delta.x() *(-1.5), -0.15, delta.z() *(-1.5)));
        private final BiFunction<Vec3, Vec3, Vec3> modifier;
        ParticleMovement(BinaryOperator<Vec3> modifier) {
            this.modifier = modifier;
        }
        ParticleMovement(Vec3 vec3) {
            this.modifier = (pos, delta) -> vec3;
        }
        public Vec3 modify(Vec3 position, Vec3 delta) {
            return modifier.apply(position, delta);
        }
    }
}
