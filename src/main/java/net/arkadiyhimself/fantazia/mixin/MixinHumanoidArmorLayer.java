package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class MixinHumanoidArmorLayer {

    @Inject(method = "renderArmorPiece*", at = @At("HEAD"), cancellable = true)
    private void melding(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity livingEntity, EquipmentSlot slot, int packedLight, HumanoidModel<LivingEntity> p_model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        EvasionHolder evasionHolder = LivingDataHelper.takeHolder(livingEntity, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) ci.cancel();

        if (!(livingEntity instanceof Player player)) return;
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() >= 3) ci.cancel();
    }
}
