package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeGui.class, remap = false)
public abstract class MixinForgeGui extends Gui {
    private Player getCameraPlayer() {
        return !(this.minecraft.getCameraEntity() instanceof Player) ? null : (Player)this.minecraft.getCameraEntity();
    }
    public MixinForgeGui(Minecraft pMinecraft, ItemRenderer pItemRenderer, long healthBlinkTime) {
        super(pMinecraft, pItemRenderer);
        this.healthBlinkTime = healthBlinkTime;
    }

    @Inject(at = @At("HEAD"), method = "renderHealth")
    private void blinkTime(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = this.getCameraPlayer();
        if (player == null) return;
        DamageSource source = player.getLastDamageSource();
        if (source != null && source.is(FTZDamageTypeTags.NOT_SHAKING_SCREEN)) healthBlinkTime = 0;
        if (displayHealth > player.getMaxHealth()) displayHealth = (int) player.getMaxHealth();
    }
}
