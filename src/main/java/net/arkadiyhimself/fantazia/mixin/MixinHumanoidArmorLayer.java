package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
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
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void melding(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int flag1, HumanoidModel<LivingEntity> model, CallbackInfo ci) {
        EvasionData evasionData = DataGetter.takeDataHolder(livingEntity, EvasionData.class);
        if (evasionData != null && evasionData.getIFrames() > 0) ci.cancel();

        if (!(livingEntity instanceof Player player)) return;
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        if (dash != null && dash.isDashing() && dash.getLevel() >= 3) ci.cancel();
    }
}
