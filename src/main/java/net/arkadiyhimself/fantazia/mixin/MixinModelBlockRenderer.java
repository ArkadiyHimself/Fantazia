package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.client.ClientEvents;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public class MixinModelBlockRenderer {

    @Unique
    private BlockPos fantazia$cachedPos = null;

    @Inject(at = @At("HEAD"), method = "putQuadData")
    private void cachePos(BlockAndTintGetter level, BlockState state, BlockPos pos, VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float brightness0, float brightness1, float brightness2, float brightness3, int lightmap0, int lightmap1, int lightmap2, int lightmap3, int packedOverlay, CallbackInfo ci) {
        this.fantazia$cachedPos = pos;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V"), method = "putQuadData")
    private void changeColor(VertexConsumer instance, PoseStack.Pose pose, BakedQuad f8, float[] brightness, float red, float green, float blue, float f1, int[] f2, int f3, boolean f4) {
        Runnable defaulted = () -> instance.putBulkData(pose, f8, brightness, red, green, blue, f1, f2, f3, f4);
        Runnable redRun = () -> instance.putBulkData(pose, f8, brightness, 1f, 0.4f, 0.4f, f1, f2, f3, f4); // if aura's range is reduced, shows blocks that are no longer affected
        Runnable purpleRun = () -> instance.putBulkData(pose, f8, brightness, 0.85f, 0.6f, 0.85f, f1, f2, f3, f4); // standard aura range
        Runnable blueRun = () -> instance.putBulkData(pose, f8, brightness, 0.55f, 0.45f, 1f, f1, f2, f3, f4); // additional aura range, if it is present

        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null || fantazia$cachedPos == null || ClientEvents.heldAuraCaster == null || Minecraft.getInstance().screen != null) {
            defaulted.run();
            return;
        }

        float basicRange = ClientEvents.heldAuraCaster.getAura().value().getRadius();

        AttributeInstance attributeInstance = localPlayer.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
        float add = attributeInstance == null ? 0 : (float) attributeInstance.getValue();

        float distance = fantazia$range(fantazia$cachedPos, localPlayer.blockPosition());
        float finalRange = basicRange + add;

        if (add < 0) {
            if (distance <= finalRange) purpleRun.run();
            else if (distance <= basicRange) redRun.run();
            else defaulted.run();
        } else {
            if (distance <= basicRange) purpleRun.run();
            else if (distance <= finalRange) blueRun.run();
            else defaulted.run();
        }
    }

    @Unique
    private float fantazia$range(BlockPos blockPos1, BlockPos blockPos2) {
        double dX = blockPos1.getX() - blockPos2.getX();
        double dY = blockPos1.getY() - blockPos2.getY();
        double dZ = blockPos1.getZ() - blockPos2.getZ();
        return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }
}
