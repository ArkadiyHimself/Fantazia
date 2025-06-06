package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.FantazicConfig;
import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.spell.SpellInstance;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.CurrentAndInitialValue;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.events.ClientEvents;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.SlotResult;

import java.awt.*;
import java.util.List;

public class FTZGuis {

    // fury effect
    private static final ResourceLocation VEINS = Fantazia.res("textures/misc/fury/veins.png");
    private static final ResourceLocation VEINS_BRIGHT = Fantazia.res("textures/misc/fury/veins_bright.png");
    private static final ResourceLocation FILLING = Fantazia.res("textures/misc/fury/filling.png");
    private static final ResourceLocation EDGES = Fantazia.res("textures/misc/fury/edges.png");

    // ancient flame
    public static final Material ANCIENT_FLAME_0 = new Material(InventoryMenu.BLOCK_ATLAS, Fantazia.res("block/ancient_flame_0"));
    public static final Material ANCIENT_FLAME_1 = new Material(InventoryMenu.BLOCK_ATLAS, Fantazia.res("block/ancient_flame_1"));

    // stuff
    protected static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/powder_snow_outline.png");

    public static final LayeredDraw.Layer FTZ_GUI = (guiGraphics, deltaTracker) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.isCreative()) return;
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null && !(screen instanceof ChatScreen)) return;

        // draw mana bar
        int x0mn = FantazicConfig.manaBarXoff.get() + 10;
        int y0mn = guiGraphics.guiHeight() + FantazicConfig.manaBarYoff.get() - 10;
        ManaHolder manaHolder = PlayerAbilityHelper.takeHolder(player, ManaHolder.class);
        if (manaHolder != null) y0mn = FantazicGui.renderMana(manaHolder, guiGraphics, x0mn, y0mn);

        // draw stamina bar
        int x0st = guiGraphics.guiWidth() - 91 + FantazicConfig.staminaBarXoff.get() - 10;
        int y0st = guiGraphics.guiHeight() + FantazicConfig.staminaBarYoff.get() - 10;
        StaminaHolder staminaHolder = PlayerAbilityHelper.takeHolder(player, StaminaHolder.class);
        if (staminaHolder != null) y0st = FantazicGui.renderStamina(staminaHolder, guiGraphics, x0st, y0st);


        // draw dash and double jump
        x0st += 3;
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isAvailable()) x0st = FantazicGui.renderDashIcon(dashHolder, guiGraphics, x0st, y0st);
        DoubleJumpHolder doubleJumpHolder = PlayerAbilityHelper.takeHolder(player, DoubleJumpHolder.class);
        if (doubleJumpHolder != null && doubleJumpHolder.isUnlocked()) FantazicGui.renderDoubleJumpIcon(doubleJumpHolder, guiGraphics, x0st, y0st);

        // euphoria
        int x0eu = 10 + FantazicConfig.euphoriaIconXoff.get();
        int y0eu = 40 + FantazicConfig.euphoriaIconYoff.get();
        EuphoriaHolder euphoriaHolder = PlayerAbilityHelper.takeHolder(player, EuphoriaHolder.class);
        if (euphoriaHolder != null) FantazicGui.renderEuphoriaBar(euphoriaHolder, guiGraphics, x0eu, y0eu);
    };

    public static final LayeredDraw.Layer OBTAINED_WISDOM = (guiGraphics, deltaTracker) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        int wisdom = ClientEvents.lastWisdom;
        int ticks = ClientEvents.wisdomTick;
        if (ticks <= 0 || wisdom <= 0) return;
        Component component = GuiHelper.bakeComponent("fantazia.gui.talent.wisdom_granted", new ChatFormatting[]{ChatFormatting.BLUE}, new ChatFormatting[]{ChatFormatting.DARK_BLUE}, wisdom);
        int x0 = (guiGraphics.guiWidth()) / 2;
        int y0 = guiGraphics.guiHeight() - 48;
        RenderSystem.enableBlend();
        float alpha = Math.min(1f, (float) ticks / 20);
        guiGraphics.setColor(1f,1f,1f,alpha);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, component, x0, y0, 0);
        guiGraphics.setColor(1f,1f,1f,1f);
        RenderSystem.disableBlend();
    };

    public static final LayeredDraw.Layer CURIO_SLOTS = (guiGraphics, deltaTracker) -> {
        FantazicGui.renderCurioSlots(guiGraphics);
    };

    public static final LayeredDraw.Layer ANCIENT_FLAME = (guiGraphics, deltaTracker) -> {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        int flameTicks;
        if (player == null || (flameTicks = player.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value()) <= 0) return;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        TextureAtlasSprite textureAtlasSprite = ANCIENT_FLAME_1.sprite();
        RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.setIdentity();
        float pX1 = -0.85f * guiGraphics.guiHeight();
        float pX2 = 0.85f * guiGraphics.guiHeight();
        float pY1 = -0.85f * guiGraphics.guiHeight();
        float pY2 = 0.65f * guiGraphics.guiHeight();

        float u0 = textureAtlasSprite.getU0();
        float u1 = textureAtlasSprite.getU1();
        float u01 = (u0 + u1) / 2.0F;
        float v0 = textureAtlasSprite.getV0();
        float v1 = textureAtlasSprite.getV1();
        float v01 = (v0 + v1) / 2.0F;
        float ratio = textureAtlasSprite.uvShrinkRatio();
        float x1 = Mth.lerp(ratio, u0, u01);
        float x2 = Mth.lerp(ratio, u1, u01);
        float y1 = Mth.lerp(ratio, v0, v01);
        float y2 = Mth.lerp(ratio, v1, v01);

        float yOff = 1.25f - Math.min(1f, (flameTicks - deltaTracker.getGameTimeDeltaTicks()) / 100) * 0.15f;
        poseStack.pushPose();
        poseStack.translate((float) guiGraphics.guiWidth() / 2, (float) guiGraphics.guiHeight() * yOff, 0);
        for (int i = 0; i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate((float)(-(i * 2 - 1)) * guiGraphics.guiWidth() * 0.25f, 0, 0.0F);
            poseStack.mulPose(Axis.YN.rotationDegrees(45f));
            Matrix4f matrix4f = poseStack.last().pose();

            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, pX1, pY1, 0f).setUv(x1, y1).setColor(1.0F, 1.0F, 1.0F, 0.2F);
            bufferbuilder.addVertex(matrix4f, pX1, pY2, 0f).setUv(x1, y2).setColor(1.0F, 1.0F, 1.0F, 0.2F);
            bufferbuilder.addVertex(matrix4f, pX2, pY2, 0f).setUv(x2, y2).setColor(1.0F, 1.0F, 1.0F, 0.2F);
            bufferbuilder.addVertex(matrix4f, pX2, pY1, 0f).setUv(x2, y1).setColor(1.0F, 1.0F, 1.0F, 0.2F);
            MeshData meshData = bufferbuilder.build();
            if (meshData != null) BufferUploader.drawWithShader(meshData);
            poseStack.popPose();
        }

        poseStack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    };

    public static final LayeredDraw.Layer AURAS = (guiGraphics, deltaTracker) -> {
        FantazicGui.renderAurasHud(guiGraphics);
    };

    public static final LayeredDraw.Layer DEVELOPER_MODE = (guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!Fantazia.DEVELOPER_MODE || player == null) return;

        Font font = Minecraft.getInstance().font;

        String string1 = "DEVELOPER MODE";
        int width1 = font.width(string1);
        guiGraphics.drawString(font, string1, guiGraphics.guiWidth() - width1, 0, 16755200);
        guiGraphics.drawString(font, "Tick count: " + player.tickCount, guiGraphics.guiWidth() - width1, 10, 16755200);
        guiGraphics.drawString(font, "Inv: " + player.invulnerableTime, guiGraphics.guiWidth() - width1, 20, 16755200);
        guiGraphics.drawString(font, "Freeze: " + player.getTicksFrozen(), guiGraphics.guiWidth() - width1, 30, 16755200);

        MeleeBlockHolder meleeBlockHolder = PlayerAbilityHelper.takeHolder(player, MeleeBlockHolder.class);
        if (meleeBlockHolder != null) {
            guiGraphics.drawString(font, "Melee block: ", guiGraphics.guiWidth() - width1, 40, 16755200);
            guiGraphics.drawString(font, "Anim: " + meleeBlockHolder.anim(), guiGraphics.guiWidth() - width1, 50, 16755200);
            guiGraphics.drawString(font, "Cooldown: " + meleeBlockHolder.blockCooldown(), guiGraphics.guiWidth() - width1, 60, 16755200);
            guiGraphics.drawString(font, "Block ticks: " + meleeBlockHolder.blockTicks(), guiGraphics.guiWidth() - width1, 70, 16755200);
            guiGraphics.drawString(font, "Parry ticks: " + meleeBlockHolder.parryTicks(), guiGraphics.guiWidth() - width1, 80, 16755200);
        }
    };

    public static final LayeredDraw.Layer FURY_VEINS = (guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        CurrentAndInitialValue holder = LivingEffectHelper.getDurationHolder(player, FTZMobEffects.FURY.value());
        if (holder == null) return;
        int dur = holder.value();
        if (dur <= 0 && dur != -1) return;
        int value = player.tickCount % 21;

        float veinTr;
        if (value > 10) veinTr = 1f - (float) (value - 10) / 10;
        else veinTr = 1f - (float) value / 10;

        float allTR = (float) Math.min(20, dur == -1 ? 20 : dur) / 20;

        GuiHelper.wholeScreen(guiGraphics, VEINS,1.0F,0,0,0.4F * allTR);
        GuiHelper.wholeScreen(guiGraphics, VEINS_BRIGHT,1.0F,0,0,veinTr * allTR);
        GuiHelper.wholeScreen(guiGraphics, FILLING,1.0F,0,0,0.45F * allTR);
        GuiHelper.wholeScreen(guiGraphics, EDGES,1.0F,0,0,0.925F * allTR);
    };

    public static final LayeredDraw.Layer FROZEN_EFFECT = (guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        CurrentAndInitialValue holder = LivingEffectHelper.getDurationHolder(player, FTZMobEffects.FROZEN.value());
        if (holder == null || holder.value() <= 0 || holder.percent() < player.getPercentFrozen()) return;
        GuiHelper.wholeScreen(guiGraphics, POWDER_SNOW_OUTLINE_LOCATION, 1f,1f,1f, holder.percent());
    };

    public static final LayeredDraw.Layer CUSTOM_BARS = (guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getAbilities().invulnerable) return;
        int x = guiGraphics.guiWidth() / 2 - 91;
        int y = guiGraphics.guiHeight() - 32 + 3;
        if (FantazicGui.renderStunBar(guiGraphics, x, y)) return;
        if (FantazicGui.renderBarrierBar(guiGraphics, x, y)) return;
        if (FantazicGui.renderLayeredBarrierBar(guiGraphics, x, y)) return;
    };
}
