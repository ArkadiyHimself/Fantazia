package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.FantazicConfig;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.AncientFlameTicksHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.FrozenEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.FuryEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
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

import java.util.List;

public class FTZGuis {

    // curio slots
    private static final ResourceLocation CURIOSLOT = Fantazia.res("textures/gui/curioslots/curioslot.png");
    private static final ResourceLocation SPELLCASTER = Fantazia.res("textures/gui/curioslots/spellcaster.png");
    private static final ResourceLocation PASSIVECASTER = Fantazia.res("textures/gui/curioslots/passivecaster.png");
    private static final ResourceLocation RECHARGE_BAR = Fantazia.res("textures/gui/curioslots/recharge_bar.png");
    private static final ResourceLocation RECHARGE_BAR_FILLING = Fantazia.res("textures/gui/curioslots/recharge_bar_filling.png");


    // aura
    private static final ResourceLocation AURA_POSITIVE = Fantazia.res("textures/gui/aura_icon/positive.png");
    private static final ResourceLocation AURA_NEGATIVE = Fantazia.res("textures/gui/aura_icon/negative.png");
    private static final ResourceLocation AURA_MIXED = Fantazia.res("textures/gui/aura_icon/mixed.png");

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

    public static final LayeredDraw.Layer FTZ_GUI = ((guiGraphics, deltaTracker) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.isCreative()) return;
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null && !(screen instanceof ChatScreen)) return;

        // draw mana bar
        int x0mn = FantazicConfig.manaBarXoff.get() + 10;
        int y0mn = guiGraphics.guiHeight() + FantazicConfig.manaBarYoff.get() - 10;
        ManaHolder manaHolder = PlayerAbilityGetter.takeHolder(player, ManaHolder.class);
        if (manaHolder != null) y0mn = FantazicGui.renderMana(manaHolder, guiGraphics, x0mn, y0mn);

        // draw stamina bar
        int x0st = guiGraphics.guiWidth() - 91 + FantazicConfig.staminaBarXoff.get() - 10;
        int y0st = guiGraphics.guiHeight() + FantazicConfig.staminaBarYoff.get() - 10;
        StaminaHolder staminaHolder = PlayerAbilityGetter.takeHolder(player, StaminaHolder.class);
        if (staminaHolder != null) y0st = FantazicGui.renderStamina(staminaHolder, guiGraphics, x0st, y0st);


        // draw dash and double jump
        x0st += 3;
        DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isAvailable()) x0st = FantazicGui.renderDashIcon(dashHolder, guiGraphics, x0st, y0st);
        DoubleJumpHolder doubleJumpHolder = PlayerAbilityGetter.takeHolder(player, DoubleJumpHolder.class);
        if (doubleJumpHolder != null && doubleJumpHolder.isUnlocked()) FantazicGui.renderDoubleJumpIcon(doubleJumpHolder, guiGraphics, x0st, y0st);

        // layered barrier
        int x0 = guiGraphics.guiWidth() / 2;
        int y0 = guiGraphics.guiHeight() - 14;
        LayeredBarrierEffect layeredBarrierEffect = LivingEffectGetter.takeHolder(player, LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) FantazicGui.renderBarrierLayers(layeredBarrierEffect, guiGraphics, x0, y0);

        // euphoria
        int x0eu = 10 + FantazicConfig.euphoriaIconXoff.get();
        int y0eu = 40 + FantazicConfig.euphoriaIconYoff.get();
        EuphoriaHolder euphoriaHolder = PlayerAbilityGetter.takeHolder(player, EuphoriaHolder.class);
        if (euphoriaHolder != null) FantazicGui.renderEuphoriaBar(euphoriaHolder, guiGraphics, x0eu, y0eu);
    });

    public static final LayeredDraw.Layer OBTAINED_WISDOM = ((guiGraphics, deltaTracker) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientValuesHolder clientValuesHolder = PlayerAbilityGetter.takeHolder(player, ClientValuesHolder.class);
        if (clientValuesHolder == null) return;
        int wisdom = clientValuesHolder.getLastWisdom();
        int ticks = clientValuesHolder.getWisdomTick();
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
    });

    public static final LayeredDraw.Layer CURIO_SLOTS = ((guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = Minecraft.getInstance().font;
        Screen screen = Minecraft.getInstance().screen;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        if (screen != null && !(screen instanceof ChatScreen)) return;

        boolean shift = Screen.hasShiftDown();

        int x = 10 + FantazicConfig.curioSlotsXoff.get();
        int y = 64 + FantazicConfig.curioSlotsYoff.get();
        List<SlotResult> spellcasters = InventoryHelper.findAllCurios(player, "spellcaster");
        List<SlotResult> passivecasters = InventoryHelper.findAllCurios(player, "passivecaster");

        int num1 = spellcasters.size();
        int num2 = passivecasters.size();

        int allSlots = num1 + num2;
        for (int i = 0; i < allSlots; i++) guiGraphics.blit(CURIOSLOT, x,y + i * 20,0,0,20,20,20,20);

        SpellInstancesHolder spellInstancesHolder = PlayerAbilityGetter.takeHolder(player, SpellInstancesHolder.class);
        for (int j = 0; j < num1; j++) {
            ItemStack item = spellcasters.get(j).stack();
            if (item.isEmpty()) guiGraphics.blit(SPELLCASTER, x,y + j * 20,0,0,20,20,20,20);
            else {
                guiGraphics.renderItem(item,x + 2,y + j * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + j * 20 + 2);

                if (item.getItem() instanceof SpellCasterItem spellCasterItem && spellInstancesHolder != null) {
                    Holder<AbstractSpell> spell = spellCasterItem.getSpell();
                    int recharge = spellInstancesHolder.getOrCreate(spell).recharge();
                    if (recharge <= 0) continue;

                    float percent = ((float) recharge / spell.value().getDefaultRecharge());
                    int fill = (int) Math.max(1, percent * 16);

                    guiGraphics.blit(RECHARGE_BAR,x + 22,y + j * 20 + 1,0,0,6,18,6,18);
                    guiGraphics.blit(RECHARGE_BAR_FILLING,x + 22 + 5, y + j * 20 + 18,0,0,-4, -fill,6,18);

                    if (shift) guiGraphics.drawString(font, Component.literal(GuiHelper.spellRecharge(recharge)).withStyle(ChatFormatting.BOLD), x + 30, y + j * 20 + 6, 16724787);
                }
            }
        }
        for (int k = 0; k < num2; k++) {
            ItemStack item = passivecasters.get(k).stack();
            if (item.isEmpty()) guiGraphics.blit(PASSIVECASTER, x,y + k * 20 + num1 * 20,0,0,20,20,20,20);
            else {
                guiGraphics.renderItem(item, x + 2, y + k * 20 + num1 * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + 2 + 20 * (num1 + k));

                if (item.getItem() instanceof SpellCasterItem spellCasterItem && spellInstancesHolder != null) {
                    Holder<AbstractSpell> spell = spellCasterItem.getSpell();
                    int recharge = spellInstancesHolder.getOrCreate(spell).recharge();
                    if (recharge <= 0) continue;

                    float percent = ((float) recharge / spell.value().getDefaultRecharge());
                    int fill = (int) Math.max(1, percent * 16);

                    guiGraphics.blit(RECHARGE_BAR,x + 22,y + k * 20 + 1 + 20 * num1,0,0,6,18,6,18);
                    guiGraphics.blit(RECHARGE_BAR_FILLING,x + 22 + 5, y + k * 20 + 18 + 20 * num1,0,0,-4, -fill,6,18);

                    if (shift) guiGraphics.drawString(font, Component.literal(GuiHelper.spellRecharge(recharge)).withStyle(ChatFormatting.BOLD), x + 30, y + k * 20 + 6 + 20 * num1, 16724787);
                }
            }
        }
    });

    public static final LayeredDraw.Layer ANCIENT_FLAME = ((guiGraphics, deltaTracker) -> {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        AncientFlameTicksHolder ancientFlameTicksHolder = LivingDataGetter.takeHolder(player, AncientFlameTicksHolder.class);
        if (ancientFlameTicksHolder == null || !ancientFlameTicksHolder.isBurning()) return;

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
        poseStack.pushPose();
        poseStack.translate((float) guiGraphics.guiWidth() / 2, (float) guiGraphics.guiHeight(), 0);
        for (int i = 0; i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate((float)(-(i * 2 - 1)) * guiGraphics.guiWidth() * 0.25f, 0, 0.0F);
            poseStack.mulPose(Axis.YN.rotationDegrees(45f));
            Matrix4f matrix4f = poseStack.last().pose();

            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, pX1, pY1, 0f).setUv(x1, y1).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            bufferbuilder.addVertex(matrix4f, pX1, pY2, 0f).setUv(x1, y2).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            bufferbuilder.addVertex(matrix4f, pX2, pY2, 0f).setUv(x2, y2).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            bufferbuilder.addVertex(matrix4f, pX2, pY1, 0f).setUv(x2, y1).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            MeshData meshData = bufferbuilder.build();
            if (meshData != null) BufferUploader.drawWithShader(meshData);
            poseStack.popPose();
        }

        poseStack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    });

    public static final LayeredDraw.Layer AURAS = ((guiGraphics, deltaTracker) -> {
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

    public static final LayeredDraw.Layer DEVELOPER_MODE = ((guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!Fantazia.DEVELOPER_MODE || player == null) return;

        Font font = Minecraft.getInstance().font;

        String string1 = "DEVELOPER MODE";
        int width1 = font.width(string1);
        guiGraphics.drawString(font, string1, guiGraphics.guiWidth() - width1, 0, 16755200);
        guiGraphics.drawString(font, "Tick count: " + player.tickCount, guiGraphics.guiWidth() - width1, 10, 16755200);
    });

    public static final LayeredDraw.Layer FURY_VEINS = ((guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        FuryEffect furyEffect = LivingEffectGetter.takeHolder(player, FuryEffect.class);
        if (furyEffect == null || !furyEffect.isFurious()) return;

        float veinTR = (float) furyEffect.getVeinTR() / 15;
        float allTR = (float) furyEffect.getBackTR() / 20;
        GuiHelper.wholeScreen(guiGraphics, VEINS, 1.0F, 0, 0, 0.4F * allTR);
        GuiHelper.wholeScreen(guiGraphics, VEINS_BRIGHT, 1.0F, 0, 0, veinTR * allTR);
        GuiHelper.wholeScreen(guiGraphics, FILLING, 1.0F, 0, 0, 0.45F * allTR);
        GuiHelper.wholeScreen(guiGraphics, EDGES, 1.0F, 0, 0, 0.925F * allTR);
    });

    public static final LayeredDraw.Layer FROZEN_EFFECT = ((guiGraphics, deltaTracker) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        FrozenEffect frozenEffect = LivingEffectGetter.takeHolder(player, FrozenEffect.class);
        if (frozenEffect == null || frozenEffect.effectPercent() <= 0 || frozenEffect.effectPercent() < player.getPercentFrozen()) return;
        GuiHelper.wholeScreen(guiGraphics, POWDER_SNOW_OUTLINE_LOCATION, 1f,1f,1f, frozenEffect.effectPercent());
    });
}
