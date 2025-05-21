package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.FantazicConfig;
import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.spell.SpellInstance;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.CurrentAndInitialValue;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotResult;

import java.awt.*;
import java.util.List;

public class FantazicGui {

    // auras gui
    private static final ResourceLocation AURA_POSITIVE_COMPONENT = Fantazia.res("container/inventory/aura/positive_component");
    private static final ResourceLocation AURA_NEGATIVE_COMPONENT = Fantazia.res("container/inventory/aura/negative_component");
    private static final ResourceLocation AURA_MIXED_COMPONENT = Fantazia.res("container/inventory/aura/mixed_component");
    private static final ResourceLocation AURA_OWNED_COMPONENT = Fantazia.res("container/inventory/aura/owned_component");

    private static final ResourceLocation AURA_POSITIVE = Fantazia.res("hud/aura/positive");
    private static final ResourceLocation AURA_NEGATIVE = Fantazia.res("hud/aura/negative");
    private static final ResourceLocation AURA_MIXED = Fantazia.res("hud/aura/mixed");
    private static final ResourceLocation AURA_OWNED = Fantazia.res("hud/aura/owned");

    // curio slots
    private static final ResourceLocation CURIOSLOT = Fantazia.res("hud/curioslots/curioslot");
    private static final ResourceLocation ACTIVECASTER = Fantazia.res("hud/curioslots/activecaster");
    private static final ResourceLocation PASSIVECASTER = Fantazia.res("hud/curioslots/passivecaster");
    private static final ResourceLocation RECHARGE_BAR = Fantazia.res("hud/curioslots/recharge_bar");
    private static final ResourceLocation RECHARGE_BAR_FILLING = Fantazia.res("hud/curioslots/recharge_bar_filling");

    // bars
    public static final ResourceLocation BARRIER_BAR_BACKGROUND = Fantazia.res("hud/bars/barrier_bar_background");
    public static final ResourceLocation BARRIER_BAR_DURATION = Fantazia.res("hud/bars/barrier_bar_duration");
    public static final ResourceLocation LAYERED_BARRIER_BAR_BACKGROUND = Fantazia.res("hud/bars/layered_barrier_bar_background");
    public static final ResourceLocation LAYERED_BARRIER_BAR_DURATION = Fantazia.res("hud/bars/layered_barrier_bar_duration");
    public static final ResourceLocation STUN_BAR_BACKGROUND = Fantazia.res("hud/bars/stun_bar_background");
    public static final ResourceLocation STUN_BAR_DURATION = Fantazia.res("hud/bars/stun_bar_duration");
    public static final ResourceLocation STUN_POINTS_BAR_BACKGROUND = Fantazia.res("hud/bars/stun_points_bar_background");
    public static final ResourceLocation STUN_POINTS_BAR_PROGRESS = Fantazia.res("hud/bars/stun_points_bar_progress");

    // mana bar stuff
    public static final ResourceLocation MANA_FRAME_TOP = Fantazia.res("textures/gui/mana/mana_frame_top.png");
    public static final ResourceLocation MANA_FRAME_BOTTOM = Fantazia.res("textures/gui/mana/mana_frame_bottom.png");
    public static final ResourceLocation MANA_FRAME_SIDE = Fantazia.res("textures/gui/mana/mana_frame_side.png");
    public static final ResourceLocation MANA_BG = Fantazia.res("textures/gui/mana/mana_background.png");
    public static final ResourceLocation MANA_ICON_EMPTY = Fantazia.res("hud/mana/mana_icon_empty");
    public static final ResourceLocation MANA_ICON = Fantazia.res("hud/mana/mana_icon");
    public static final ResourceLocation MANA_ICON_HALF = Fantazia.res("hud/mana/mana_icon_half");
    // stamina bar stuff

    public static final ResourceLocation STAMINA_FRAME_TOP = Fantazia.res("textures/gui/stamina/stamina_frame_top.png");
    public static final ResourceLocation STAMINA_FRAME_BOTTOM = Fantazia.res("textures/gui/stamina/stamina_frame_bottom.png");
    public static final ResourceLocation STAMINA_FRAME_SIDE = Fantazia.res("textures/gui/stamina/stamina_frame_side.png");
    public static final ResourceLocation STAMINA_BG = Fantazia.res("textures/gui/stamina/stamina_background.png");
    public static final ResourceLocation STAMINA_ICON_EMPTY = Fantazia.res("hud/stamina/stamina_icon_empty");
    public static final ResourceLocation STAMINA_ICON = Fantazia.res("hud/stamina/stamina_icon");
    public static final ResourceLocation STAMINA_ICON_HALF = Fantazia.res("hud/stamina/stamina_icon_half");

    // dash ability stuff
    private static final ResourceLocation DASH1_EMPTY = Fantazia.res("hud/ability/dash1_empty");
    private static final ResourceLocation DASH2_EMPTY = Fantazia.res("hud/ability/dash2_empty");
    private static final ResourceLocation DASH3_EMPTY = Fantazia.res("hud/ability/dash3_empty");
    private static final ResourceLocation DASH1 = Fantazia.res("hud/ability/dash1");
    private static final ResourceLocation DASH2 = Fantazia.res("hud/ability/dash2");
    private static final ResourceLocation DASH3 = Fantazia.res("hud/ability/dash3");

    // double jump stuff
    private static final ResourceLocation DOUBLE_JUMP = Fantazia.res("hud/ability/double_jump");
    private static final ResourceLocation DOUBLE_JUMP_EMPTY = Fantazia.res("hud/ability/double_jump_empty");

    // layered barrier stuff
    private static final ResourceLocation BARRIER_LAYERS = Fantazia.res("hud/barrier_layers");

    // euphoria
    private static final ResourceLocation EUPHORIA = Fantazia.res("hud/euphoria/icon");
    private static final ResourceLocation EUPHORIA_EMPTY = Fantazia.res("hud/euphoria/empty");

    public static boolean renderStunBar(GuiGraphics guiGraphics, int x, int y) {
        StunEffectHolder stunEffectHolder = LivingEffectHelper.takeHolder(Minecraft.getInstance().player, StunEffectHolder.class);
        if (stunEffectHolder == null || !stunEffectHolder.renderBar()) return false;
        int percent;
        if (stunEffectHolder.stunned()) {
            percent = (int) ((float) stunEffectHolder.duration() / (float) stunEffectHolder.initialDuration() * 182);
            guiGraphics.blitSprite(STUN_BAR_BACKGROUND, x, y, 182, 5);
            guiGraphics.blitSprite(STUN_BAR_DURATION,182, 5,0,0, x, y, percent, 5);
        } else if (stunEffectHolder.hasPoints()) {
            percent = (int) ((float) stunEffectHolder.getPoints() / (float) stunEffectHolder.getMaxPoints() * 182);
            guiGraphics.blitSprite(STUN_POINTS_BAR_BACKGROUND, x, y, 182, 5);
            guiGraphics.blitSprite(STUN_POINTS_BAR_PROGRESS, 182, 5,0,0, x, y, percent, 5);
        }
        return true;
    }

    public static boolean renderBarrierBar(GuiGraphics guiGraphics, int x, int y) {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = Minecraft.getInstance().font;
        if (player == null) return false;
        CurrentAndInitialValue values = LivingEffectHelper.getDurationHolder(player, FTZMobEffects.BARRIER.value());
        if (values == null) return false;
        int percent = (int) (values.percent() * 182);
        if (percent <= 0) return false;
        guiGraphics.blitSprite(BARRIER_BAR_BACKGROUND, x, y, 182, 5);
        guiGraphics.blitSprite(BARRIER_BAR_DURATION, 182, 5,0,0, x, y, percent, 5);
        String amount = String.valueOf(player.getData(FTZAttachmentTypes.BARRIER_HEALTH));
        int y0 = guiGraphics.guiHeight() - 35;
        int x0 = (int) ((float) guiGraphics.guiWidth() / 2 - ((float) font.width(amount) / 2));

        //guiGraphics.drawString(font, amount, x0, y0, 8780799, true);
        guiGraphics.drawString(font, amount, x0 + 1, y0, 0, false);
        guiGraphics.drawString(font, amount, x0 - 1, y0, 0, false);
        guiGraphics.drawString(font, amount, x0, y0 + 1, 0, false);
        guiGraphics.drawString(font, amount, x0, y0 - 1, 0, false);
        guiGraphics.drawString(font, amount, x0, y0, 8780799, false);
        return true;
    }

    public static boolean renderLayeredBarrierBar(GuiGraphics guiGraphics, int x, int y) {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = Minecraft.getInstance().font;
        if (player == null) return false;
        CurrentAndInitialValue values = LivingEffectHelper.getDurationHolder(Minecraft.getInstance().player, FTZMobEffects.LAYERED_BARRIER.value());
        if (values == null) return false;
        int percent = (int) (values.percent() * 182);
        if (percent <= 0) return false;
        guiGraphics.blitSprite(LAYERED_BARRIER_BAR_BACKGROUND, x, y, 182, 5);
        guiGraphics.blitSprite(LAYERED_BARRIER_BAR_DURATION, 182, 5,0,0, x, y, percent, 5);

        String amo = "×" + player.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS);
        int y0 = y - 18;
        int x0 = (guiGraphics.guiWidth() - font.width(amo)) / 2 - 5;
        guiGraphics.blitSprite(BARRIER_LAYERS, x0, y0,9,9);
        guiGraphics.drawString(font, amo, x0 + 10, y0 - 1, 8780799, true);
        return true;
    }

    public static void renderAurasInventory(GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Screen screen = Minecraft.getInstance().screen;

        int imgWDT;
        int imgHGT;
        if (screen instanceof InventoryScreen) {
            imgWDT = 176;
            imgHGT = 166;
        } else if (screen instanceof CreativeModeInventoryScreen) {
            imgWDT = 195;
            imgHGT = 136;
        } else return;

        int leftPos = (screen.width - imgWDT) / 2;
        int x = leftPos - 82;
        int topPos = (screen.height - imgHGT) / 2;

        List<AuraInstance> playerAuras = AuraHelper.getAllAffectingAuras(player);
        if (playerAuras.isEmpty()) return;

        if (Fantazia.DEVELOPER_MODE) guiGraphics.drawString(font, "Affecting auras: " + playerAuras.size(), 0, 180, 16777215);
        for (int i = 0; i < Math.min(playerAuras.size(), 9); i++) {
            AuraInstance instance = playerAuras.get(i);
            Holder<Aura> aura = instance.getAura();

            boolean owned = instance.getOwner() == player;

            int y = topPos + i * 22;
            ResourceLocation location = owned ? AURA_OWNED_COMPONENT : switch (aura.value().getType()) {
                case POSITIVE -> AURA_POSITIVE_COMPONENT;
                case NEGATIVE -> AURA_NEGATIVE_COMPONENT;
                case MIXED -> AURA_MIXED_COMPONENT;
            };
            guiGraphics.blitSprite(location, x, y,80,20);
            ResourceLocation icon = Aura.getIcon(aura);

            MutableComponent name = aura.value().getAuraComponent();
            if (name != null) {
                name.withStyle(aura.value().tooltipFormatting());

                if (owned) name.withStyle(ChatFormatting.GOLD);

                int length = font.width(name);
                if (!aura.value().secondary(player, instance.getOwner()))
                    RenderSystem.setShaderColor(0.65f, 0.65f, 0.65f, 0.65f);
                guiGraphics.blit(icon, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

                if (length > 60) guiGraphics.drawString(Minecraft.getInstance().font, name, x + 20, y + 6, 0);
                else guiGraphics.drawCenteredString(Minecraft.getInstance().font, name, x + 48, y + 6, 0);
            }


            if (!aura.value().buildIconTooltip().isEmpty()) {
                int mouseX = (int)(Minecraft.getInstance().mouseHandler.xpos() * (double)guiGraphics.guiWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth());
                int mouseY = (int)(Minecraft.getInstance().mouseHandler.ypos() * (double)guiGraphics.guiHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight());
                if (FantazicMath.within(x,x + 80, mouseX) && FantazicMath.within(y,y + 20, mouseY)) guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, aura.value().buildIconTooltip(), mouseX, mouseY);
            }
        }
    }

    public static void renderAurasHud(GuiGraphics guiGraphics) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (Minecraft.getInstance().screen != null && !(Minecraft.getInstance().screen instanceof ChatScreen)) return;
        List<AuraInstance> auras = AuraHelper.getAllAffectingAuras(player);
        for (int i = 0; i < Math.min(auras.size(), 6); i++) {
            AuraInstance auraInstance = auras.get(i);
            Holder<Aura> aura = auraInstance.getAura();

            boolean owned = auraInstance.getOwner() == player;

            int x = 2 + i * 22;
            int y = 2;
            ResourceLocation location = owned ? AURA_OWNED : switch (aura.value().getType()) {
                case POSITIVE -> AURA_POSITIVE;
                case NEGATIVE -> AURA_NEGATIVE;
                case MIXED -> AURA_MIXED;
            };

            guiGraphics.blitSprite(location, x, y, 20,20);
            ResourceLocation icon = Aura.getIcon(aura);
            if (!aura.value().secondary(player, auraInstance.getOwner())) RenderSystem.setShaderColor(0.65f,0.65f,0.65f,0.65f);
            guiGraphics.blit(icon, x + 2, y + 2, 0,0,16,16,16,16);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
        }
    }

    public static int renderMana(@NotNull ManaHolder manaHolder, GuiGraphics guiGraphics, int x0, int y0) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return y0;
        int maxMana = Mth.ceil(manaHolder.getMaxMana() / 2);

        int fullMaxRows = (int) ((float) maxMana / 10);
        boolean flag1 = maxMana % 10 > 0;
        int maxRows = flag1 ? fullMaxRows + 1 : fullMaxRows;

        int frHGT = -3 + 8 * maxRows;

        guiGraphics.setColor(1f, 1f,1f,0.5f);
        guiGraphics.blit(MANA_BG, x0 + 3, y0 - 11 - frHGT, 0,0, 85, frHGT + 8, 128,128);
        guiGraphics.setColor(1f, 1f,1f,1f);

        guiGraphics.blit(MANA_FRAME_BOTTOM, x0, y0 - 8, 0,8,91,8,91,8);
        guiGraphics.blit(MANA_FRAME_SIDE, x0, y0 - 6 - frHGT, 0,0, 91, frHGT, 91, 16);
        guiGraphics.blit(MANA_FRAME_TOP, x0, y0 - 14 - frHGT, 0,0, 91, 8, 91, 8);

        int pX = x0 + 5;
        int pY = y0 - 14;

        for (int i = maxMana - 1; i >= 0; i--) {
            int j1 = i / 10;
            int k1 = i % 10;
            int l1 = pX + k1 * 8;
            int i2 = pY - j1 * 8;
            guiGraphics.blitSprite(MANA_ICON_EMPTY, l1, i2,9,9);

            int j2 = i * 2;

            if (j2 < manaHolder.getMana()) {
                ResourceLocation location = j2 + 1 == Mth.ceil(manaHolder.getMana()) ? MANA_ICON_HALF : MANA_ICON;
                guiGraphics.blitSprite(location, l1, i2, 9,9);
            }
        }
        return y0 - 35 - frHGT;
    }

    public static int renderStamina(@NotNull StaminaHolder staminaHolder, GuiGraphics guiGraphics, int x0, int y0) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return y0;
        int maxStamina = Mth.ceil(staminaHolder.getMaxStamina() / 2);

        int fullMaxRows = (int) ((float) maxStamina / 10);
        boolean flag1 = maxStamina % 10 > 0;
        int maxRows = flag1 ? fullMaxRows + 1 : fullMaxRows;

        int frHGT = -3 + 8 * maxRows;

        guiGraphics.setColor(1f, 1f,1f,0.5f);
        guiGraphics.blit(STAMINA_BG, x0 + 3, y0 - 11 - frHGT, 0,0, 85, frHGT + 8, 128,128);
        guiGraphics.setColor(1f, 1f,1f,1f);

        guiGraphics.blit(STAMINA_FRAME_BOTTOM, x0, y0 - 8, 0,8,91,8,91,8);
        guiGraphics.blit(STAMINA_FRAME_SIDE, x0, y0 - 6 - frHGT, 0,0, 91, frHGT, 91, 16);
        guiGraphics.blit(STAMINA_FRAME_TOP, x0, y0 - 14 - frHGT, 0,0, 91, 8, 91, 8);

        int pX = x0 + 5;
        int pY = y0 - 14;

        for (int i = maxStamina - 1; i >= 0; i--) {
            int j1 = i / 10;
            int k1 = i % 10;
            int l1 = pX + k1 * 8;
            int i2 = pY - j1 * 8;
            guiGraphics.blitSprite(STAMINA_ICON_EMPTY, l1, i2,9,9);

            int j2 = i * 2;

            if (j2 < staminaHolder.getStamina()) {
                ResourceLocation location = j2 + 1 == Mth.ceil(staminaHolder.getStamina()) ? STAMINA_ICON_HALF : STAMINA_ICON;
                guiGraphics.blitSprite(location, l1, i2, 9,9);
            }
        }
        return y0 - frHGT - 35;
    }

    public static int renderDashIcon(@NotNull DashHolder dashHolder, GuiGraphics guiGraphics, int x0, int y0) {
        ResourceLocation emptyIcon = switch (dashHolder.getLevel()) {
            case 1 -> DASH1_EMPTY;
            case 2 -> DASH2_EMPTY;
            case 3 -> DASH3_EMPTY;
            default -> null;
        };
        ResourceLocation dashIcon = switch (dashHolder.getLevel()) {
            case 1 -> DASH1;
            case 2 -> DASH2;
            case 3 -> DASH3;
            default -> null;
        };
        if (dashIcon == null || emptyIcon == null) return x0;

        int filling;
        if (dashHolder.isDashing()) filling = 20 - (int) ((float) dashHolder.getDur() / (float) dashHolder.getInitDur() * 20);
        else filling = (int) ((float) dashHolder.getRecharge() / (float) dashHolder.getInitRecharge() * 20);

        guiGraphics.blitSprite(emptyIcon, x0, y0, 20,20);
        guiGraphics.blitSprite(dashIcon, 20, 20, 0, 0,x0, y0, 20 - filling, 20);
        return x0 + 23;
    }

    public static void renderDoubleJumpIcon(@NotNull DoubleJumpHolder doubleJumpHolder, GuiGraphics guiGraphics, int x0, int y0) {
        int recharge = doubleJumpHolder.getRecharge();
        if (recharge > 0) {
            int filling = (int) ((float) recharge / (float) doubleJumpHolder.elytraRecharge() * 20);
            guiGraphics.blitSprite(DOUBLE_JUMP_EMPTY, x0, y0, 20,20);
            guiGraphics.blitSprite(DOUBLE_JUMP, 20, 20, 0, 0, x0, y0, 20 - filling, 20);
            return;
        }
        ResourceLocation icon = doubleJumpHolder.canJump() ? DOUBLE_JUMP : DOUBLE_JUMP_EMPTY;
        guiGraphics.blitSprite(icon, x0, y0,20,20);
    }

    public static void renderEuphoriaBar(@NotNull EuphoriaHolder euphoriaHolder, GuiGraphics guiGraphics, int x0, int y0) {
        int combo = Math.min(10, euphoriaHolder.kills());
        int ticks = euphoriaHolder.ticks();
        if (combo <= 1) return;

        int halfDur = EuphoriaHolder.TICKS / 2;
        int phase = ticks - halfDur;
        float alpha = ticks < halfDur ? 0.45f + (float) FantazicMath.intoCos(phase, 20) * 0.275f : 1f;
        float red = euphoriaHolder.comboPercent();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(red * 0.4f + 0.6f,1f,1f, alpha);

        guiGraphics.blitSprite(EUPHORIA_EMPTY, x0, y0,20,20);
        guiGraphics.blitSprite(EUPHORIA, 20, 20, 0, 0, x0, y0, 2 * combo, 20);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f,1f,1f,1f);

        if (!Fantazia.DEVELOPER_MODE || Minecraft.getInstance().player == null) return;
        guiGraphics.drawString(Minecraft.getInstance().font, "Combo: " + combo,x0 + 24, y0,0);
        guiGraphics.drawString(Minecraft.getInstance().font, "Ticks: " + ticks,x0 + 24, y0 + 8,0);

        AttributeInstance attack = Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_SPEED);
        AttributeInstance movement = Minecraft.getInstance().player.getAttribute(Attributes.MOVEMENT_SPEED);

        double attr1 = attack == null ? 0 : attack.getValue();
        double attr2 = movement == null ? 0 : movement.getValue();

        guiGraphics.drawString(Minecraft.getInstance().font, "Attack: " + attr1,x0 + 24, y0 + 16,0);
        guiGraphics.drawString(Minecraft.getInstance().font, "Movement: " + attr2,x0 + 24, y0 + 24,0);
    }

    public static void renderCurioSlots(GuiGraphics guiGraphics) {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = Minecraft.getInstance().font;
        Screen screen = Minecraft.getInstance().screen;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        if (screen != null && !(screen instanceof ChatScreen)) return;

        boolean shift = Screen.hasShiftDown();
        PoseStack poseStack = guiGraphics.pose();

        int x = 10 + FantazicConfig.curioSlotsXoff.get();
        int y = 64 + FantazicConfig.curioSlotsYoff.get();
        List<SlotResult> activecasters = FantazicUtil.findAllCurios(player, "activecaster");
        List<SlotResult> passivecasters = FantazicUtil.findAllCurios(player, "passivecaster");

        int num1 = activecasters.size();
        int num2 = passivecasters.size();

        for (int i = 0; i < num1 + num2; i++) guiGraphics.blitSprite(CURIOSLOT, x, y + i * 20,20,20);

        int lowManaColor = new Color(65, 180, 255, 95).getRGB();

        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(player, SpellInstancesHolder.class);
        for (int j = 0; j < num1; j++) {
            int y0 = y + j * 20;
            ItemStack item = activecasters.get(j).stack();
            if (item.isEmpty()) guiGraphics.blitSprite(ACTIVECASTER, x, y0,20,20);
            else {
                guiGraphics.renderItem(item,x + 2,y0 + 2);

                if (item.getItem() instanceof SpellCasterItem spellCasterItem && spellInstancesHolder != null) {
                    Holder<AbstractSpell> spell = spellCasterItem.getSpell();
                    SpellInstance instance = spellInstancesHolder.getOrCreate(spell);
                    int recharge = instance.recharge();
                    float manaCost = instance.getSpell().value().getManacost();
                    if (!PlayerAbilityHelper.enoughMana(player, manaCost)) {
                        poseStack.pushPose();
                        poseStack.translate(0,0,200);
                        guiGraphics.fill(x + 2, y0 + 2, x + 18, y0 + 18, lowManaColor);
                        poseStack.popPose();
                    }

                    if (recharge <= 0) continue;
                    float percent = ((float) recharge / spell.value().getDefaultRecharge());
                    int fill = (int) Math.max(1, percent * 16);

                    guiGraphics.blitSprite(RECHARGE_BAR,x + 22,y0 + 1,6,18);
                    guiGraphics.blitSprite(RECHARGE_BAR_FILLING, 6, 18, 6, 18, x + 22 + 5, y0 + 18, -4, -fill);

                    if (shift) guiGraphics.drawString(font, Component.literal(GuiHelper.spellRecharge(recharge)).withStyle(ChatFormatting.BOLD), x + 30, y + j * 20 + 6, 16724787);
                }
            }
        }
        for (int k = 0; k < num2; k++) {
            int y0 = y + k * 20;
            ItemStack item = passivecasters.get(k).stack();
            if (item.isEmpty()) guiGraphics.blitSprite(PASSIVECASTER, x,y + k * 20 + num1 * 20,20,20);
            else {
                guiGraphics.renderItem(item, x + 2, y + k * 20 + num1 * 20 + 2);

                if (item.getItem() instanceof SpellCasterItem spellCasterItem && spellInstancesHolder != null) {
                    Holder<AbstractSpell> spell = spellCasterItem.getSpell();

                    SpellInstance instance = spellInstancesHolder.getOrCreate(spell);
                    int recharge = instance.recharge();
                    float manaCost = instance.getSpell().value().getManacost();
                    if (!PlayerAbilityHelper.enoughMana(player, manaCost)) {
                        poseStack.pushPose();
                        poseStack.translate(0,0,200);
                        guiGraphics.fill(x + 2, y0 + 2, x + 18, y0 + 18, lowManaColor);
                        poseStack.popPose();
                    }

                    if (recharge <= 0) continue;
                    float percent = ((float) recharge / spell.value().getDefaultRecharge());
                    int fill = (int) Math.max(1, percent * 16);

                    guiGraphics.blitSprite(RECHARGE_BAR,x + 22,y + k * 20 + 1 + 20 * num1,6,18);
                    guiGraphics.blitSprite(RECHARGE_BAR_FILLING, 6, 18, 6, 18, x + 22 + 5, y + k * 20 + 18 + 20 * num1, -4, -fill);

                    if (shift) guiGraphics.drawString(font, Component.literal(GuiHelper.spellRecharge(recharge)).withStyle(ChatFormatting.BOLD), x + 30, y + k * 20 + 6 + 20 * num1, 16724787);
                }
            }
        }
    }
}
