package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.*;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.DarkFlameTicks;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.FrozenEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.FuryEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

public class FTZGuis {
    private static final ResourceLocation CURIOSLOT = Fantazia.res("textures/gui/curioslots/curioslot.png");
    private static final ResourceLocation SPELLCASTER = Fantazia.res("textures/gui/curioslots/spellcaster.png");
    private static final ResourceLocation PASSIVECASTER = Fantazia.res("textures/gui/curioslots/passivecaster.png");
    private static final ResourceLocation AURA_POSITIVE = Fantazia.res("textures/gui/aura_icon/positive.png");
    private static final ResourceLocation AURA_NEGATIVE = Fantazia.res("textures/gui/aura_icon/negative.png");
    private static final ResourceLocation AURA_MIXED = Fantazia.res("textures/gui/aura_icon/mixed.png");
    private static final ResourceLocation VEINS = Fantazia.res("textures/misc/fury/veins.png");
    private static final ResourceLocation VEINS_BRIGHT = Fantazia.res("textures/misc/fury/veins_bright.png");
    private static final ResourceLocation FILLING = Fantazia.res("textures/misc/fury/filling.png");
    private static final ResourceLocation EDGES = Fantazia.res("textures/misc/fury/edges.png");
    public static final Material ANCIENT_FLAME_0 = new Material(TextureAtlas.LOCATION_BLOCKS, Fantazia.res("block/ancient_flame_0"));
    public static final Material ANCIENT_FLAME_1 = new Material(TextureAtlas.LOCATION_BLOCKS, Fantazia.res("block/ancient_flame_1"));
    protected static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = new ResourceLocation("textures/misc/powder_snow_outline.png");
    public static final IGuiOverlay FTZ_GUI = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.isCreative()) return;

        // draw mana bar
        int x0mn = 0;
        int y0mn = screenHeight;
        ManaData manaData = AbilityGetter.takeAbilityHolder(player, ManaData.class);
        if (manaData != null) y0mn = FantazicGui.renderMana(manaData, guiGraphics, x0mn, y0mn);

        // draw stamina bar
        int x0st = screenWidth - 91;
        int y0st = screenHeight;
        StaminaData staminaData = AbilityGetter.takeAbilityHolder(player, StaminaData.class);
        if (staminaData != null) y0st = FantazicGui.renderStamina(staminaData, guiGraphics, x0st, y0st);

        x0st += 3;
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        if (dash != null && dash.isAvailable()) x0st = FantazicGui.renderDashIcon(dash, guiGraphics, x0st, y0st);
        DoubleJump doubleJump = AbilityGetter.takeAbilityHolder(player, DoubleJump.class);
        if (doubleJump != null && doubleJump.isUnlocked()) FantazicGui.renderDoubleJumpIcon(doubleJump, guiGraphics, x0st, y0st);

        int x0 = screenWidth / 2;
        int y0 = screenHeight - 14;
        LayeredBarrierEffect layeredBarrierEffect = EffectGetter.takeEffectHolder(player, LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) FantazicGui.renderBarrierLayers(layeredBarrierEffect, guiGraphics, x0, y0);
    });
    public static final IGuiOverlay OBTAINED_WISDOM = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientValues clientValues = AbilityGetter.takeAbilityHolder(player, ClientValues.class);
        if (clientValues == null) return;
        int wisdom = clientValues.getLastWisdom();
        int ticks = clientValues.getWisdomTick();
        if (ticks <= 0 || wisdom <= 0) return;
        Component component = GuiHelper.bakeComponent("fantazia.gui.talent.wisdom_granted", new ChatFormatting[]{ChatFormatting.BLUE}, new ChatFormatting[]{ChatFormatting.DARK_BLUE}, wisdom);
        int x0 = (screenWidth) / 2;
        int y0 = screenHeight - 48;
        RenderSystem.enableBlend();
        float alpha = Math.min(1f, (float) ticks / 20);
        guiGraphics.setColor(1f,1f,1f,alpha);
        guiGraphics.drawCenteredString(gui.getFont(), component, x0, y0, 0);
        guiGraphics.setColor(1f,1f,1f,1f);
        RenderSystem.disableBlend();
    }));
    public static final IGuiOverlay CURIO_SLOTS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = gui.getFont();
        Screen screen = Minecraft.getInstance().screen;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        if (screen != null && !(screen instanceof ChatScreen)) return;
        int x = 2;
        int y = 80;
        List<SlotResult> spellcasters = InventoryHelper.findAllCurios(player, "spellcaster");
        List<SlotResult> passivecasters = InventoryHelper.findAllCurios(player, "passivecaster");

        int num1 = spellcasters.size();
        int num2 = passivecasters.size();

        int allSlots = num1 + num2;
        for (int i = 0; i < allSlots; i++) guiGraphics.blit(CURIOSLOT, x,y + i * 20,0,0,20,20,20,20);

        for (int j = 0; j < num1; j++) {
            ItemStack item = spellcasters.get(j).stack();
            if (item.isEmpty()) guiGraphics.blit(SPELLCASTER, x,y + j * 20,0,0,20,20,20,20);
            else {
                guiGraphics.renderItem(item,x + 2,y + j * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + j * 20 + 2);
            }
        }
        for (int k = 0; k < num2; k++) {
            ItemStack item = passivecasters.get(k).stack();
            if (item.isEmpty()) guiGraphics.blit(PASSIVECASTER, x,y + k * 20 + num1 * 20,0,0,20,20,20,20);
            else {
                guiGraphics.renderItem(item, x + 2, y + k * 20 + num1 * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + 2 + 20 * (num1 + k));
            }
        }
    });
    public static final IGuiOverlay ANCIENT_FLAME = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        DarkFlameTicks darkFlameTicks = DataGetter.takeDataHolder(player, DarkFlameTicks.class);;
        if (darkFlameTicks == null || !darkFlameTicks.isBurning()) return;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        TextureAtlasSprite textureAtlasSprite = ANCIENT_FLAME_1.sprite();
        RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.setIdentity();
        float pX1 = -0.85f * screenHeight;
        float pX2 = 0.85f * screenHeight;
        float pY1 = -0.85f * screenHeight;
        float pY2 = 0.65f * screenHeight;

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
        poseStack.pushPose();
        poseStack.translate((float) screenWidth / 2, (float) screenHeight, 0);
        for (int i = 0; i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate((float)(-(i * 2 - 1)) * screenWidth * 0.25f, 0, 0.0F);
            poseStack.mulPose(Axis.YN.rotationDegrees(45f));
            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, pX1, pY1, 0f).uv(x1, y1).endVertex();
            bufferbuilder.vertex(matrix4f, pX1, pY2, 0f).uv(x1, y2).endVertex();
            bufferbuilder.vertex(matrix4f, pX2, pY2, 0f).uv(x2, y2).endVertex();
            bufferbuilder.vertex(matrix4f, pX2, pY1, 0f).uv(x2, y1).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();
        }
        poseStack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    });
    public static final IGuiOverlay AURAS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (Minecraft.getInstance().screen != null && !(Minecraft.getInstance().screen instanceof ChatScreen)) return;
        List<AuraInstance<Player>> auras = AuraHelper.getAffectingAuras(player);
        for (int i = 0; i < Math.min(auras.size(), 6); i++) {
            AuraInstance<Player> auraInstance = auras.get(i);
            BasicAura<Player> aura = auraInstance.getAura();
            int x = 2 + i * 22;
            int y = 2;
            ResourceLocation location = switch (aura.getType()) {
                case POSITIVE -> AURA_POSITIVE;
                case NEGATIVE -> AURA_NEGATIVE;
                case MIXED -> AURA_MIXED;
            };
            guiGraphics.blit(location, x, y, 0,0,20,20,20,20);
            ResourceLocation icon = aura.getIcon();
            if (!aura.secondary(player, auraInstance.getOwner())) RenderSystem.setShaderColor(0.65f,0.65f,0.65f,0.65f);
            guiGraphics.blit(icon, x + 2, y + 2, 0,0,16,16,16,16);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
        }
    });
    public static IGuiOverlay DEVELOPER_MODE = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!Fantazia.DEVELOPER_MODE || player == null) return;

        Font font = Minecraft.getInstance().font;

        String string1 = "DEVELOPER MODE";
        int width1 = font.width(string1);
        guiGraphics.drawString(font, string1, screenWidth - width1,0,16755200);
    });
    public static IGuiOverlay FURY_VEINS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        FuryEffect furyEffect = EffectGetter.takeEffectHolder(player, FuryEffect.class);
        if (furyEffect == null || !furyEffect.isFurious()) return;
        gui.setupOverlayRenderState(true, true);
        float veinTR = (float) furyEffect.getVeinTR() / 15;
        float allTR = (float) furyEffect.getBackTR() / 20;
        GuiHelper.wholeScreen(VEINS, 1.0F, 0, 0, 0.4F * allTR);
        GuiHelper.wholeScreen(VEINS_BRIGHT, 1.0F, 0, 0, veinTR * allTR);
        GuiHelper.wholeScreen(FILLING, 1.0F, 0, 0, 0.45F * allTR);
        GuiHelper.wholeScreen(EDGES, 1.0F, 0, 0, 0.925F * allTR);
    });
    public static IGuiOverlay FROZEN_EFFECT = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        FrozenEffect frozenEffect = EffectGetter.takeEffectHolder(player, FrozenEffect.class);
        if (frozenEffect == null || frozenEffect.effectPercent() <= 0 || frozenEffect.effectPercent() < player.getPercentFrozen()) return;
        gui.setupOverlayRenderState(true, false);
        GuiHelper.wholeScreen(POWDER_SNOW_OUTLINE_LOCATION, 1f,1f,1f, frozenEffect.effectPercent());
        gui.setupOverlayRenderState(false,false);
    });
}
