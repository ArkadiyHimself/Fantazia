package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.RenderBuffers;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.CombHealthHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = Gui.class, remap = false)
public abstract class MixinGui {

    @Unique
    private static final ResourceLocation HONEY_HEART = Fantazia.location("hud/heart/honey_heart");
    @Unique
    private static final ResourceLocation HONEY_HEART_RIGHT = Fantazia.location("hud/heart/honey_heart_right");
    @Unique
    private static final ResourceLocation HONEY_HEART_LEFT = Fantazia.location("hud/heart/honey_heart_left");


    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Shadow private long healthBlinkTime;

    @Shadow private int displayHealth;

    @Shadow @Final private RandomSource random;

    @Shadow protected abstract void renderHeart(GuiGraphics guiGraphics, Gui.HeartType heartType, int x, int y, boolean hardcore, boolean halfHeart, boolean blinking);

    @Shadow protected abstract void renderSlot(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed);

    @Inject(at = @At("HEAD"), method = "renderHealthLevel")
    private void blinkTime(GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = this.getCameraPlayer();
        if (player == null) return;
        DamageSource source = player.getLastDamageSource();
        if (source != null && source.is(FTZDamageTypeTags.NOT_SHAKING_SCREEN)) this.healthBlinkTime = 0;
        if (this.displayHealth > player.getMaxHealth()) displayHealth = (int) player.getMaxHealth();
    }

    @Inject(
            at = @At(
                    value = "TAIL"
            ),
            method = "renderHearts"
    )
    private void renderHoneyHeart(
            GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo ci
    ) {
        CombHealthHolder holder = player.getData(FTZAttachmentTypes.COMB_HEALTH);
        int ticks = holder.ticks();
        int heal = Mth.ceil(holder.toHeal());
        if (ticks <= 0 || heal <= 0) return;

        float alpha = 1f - (float) FantazicMath.intoCos(ticks, 25) * 0.65f;
        int yOff = 0;

        boolean rightHeart = currentHealth % 2 == 1;
        int finalHealth = currentHealth + heal;


        int i = Mth.floor((double) currentHealth / 2.0);
        int g = Mth.floor((double) finalHealth / 2.0);

        if (rightHeart) {
            int i1 = i / 10;
            int j1 = i % 10;
            int k1 = x + j1 * 8;
            int l1 = y - i1 * height - yOff;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1f,1f,1f, alpha);
            guiGraphics.blitSprite(HONEY_HEART_RIGHT, k1, l1,9,9);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            RenderSystem.disableBlend();

            heal--;
            currentHealth++;
            if (heal <= 0) return;
        }

        boolean leftHeart = finalHealth % 2 == 1;

        if (leftHeart && finalHealth <= maxHealth) {
            int i1 = g / 10;
            int j1 = g % 10;
            int k1 = x + j1 * 8;
            int l1 = y - i1 * height - yOff;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1f,1f,1f, alpha);
            guiGraphics.blitSprite(HONEY_HEART_LEFT, k1, l1,9,9);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            RenderSystem.disableBlend();

            heal--;
            if (heal <= 0) return;
        }

        int lastHeart = Mth.floor((double) (currentHealth + heal) / 2);
        int firstHeart = Mth.floor((double) currentHealth / 2);

        for (int l = lastHeart - 1; l >= firstHeart; --l) {
            int i1 = l / 10;
            int j1 = l % 10;
            int k1 = x + j1 * 8;
            int l1 = y - i1 * height - yOff;
            if (currentHealth + absorptionAmount <= 4) {
                l1 += random.nextInt(2);
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1f,1f,1f, alpha);
            guiGraphics.blitSprite(HONEY_HEART, k1, l1,9,9);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            RenderSystem.disableBlend();
        }
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            method = "renderItemHotbar"
    )
    private void renderSlot(Gui instance, GuiGraphics guiGraphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack stack, int k) {
        if (!stack.isEmpty()) RenderBuffers.RENDER_RECHARGEABLE_TOOL_DATA = true;
        renderSlot(guiGraphics, i, j, deltaTracker, player, stack, k);
    }
}
