package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = Gui.class, remap = false)
public abstract class MixinGui {

    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Shadow private long healthBlinkTime;

    @Shadow private int displayHealth;

    @Inject(at = @At("HEAD"), method = "renderHealthLevel")
    private void blinkTime(GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = this.getCameraPlayer();
        if (player == null) return;
        DamageSource source = player.getLastDamageSource();
        if (source != null && source.is(FTZDamageTypeTags.NOT_SHAKING_SCREEN)) this.healthBlinkTime = 0;
        if (this.displayHealth > player.getMaxHealth()) displayHealth = (int) player.getMaxHealth();
    }
}
