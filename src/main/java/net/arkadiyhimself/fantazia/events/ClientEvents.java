package net.arkadiyhimself.fantazia.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.KeyBinding;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.DarkFlameTicks;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.*;
import net.arkadiyhimself.fantazia.client.gui.FantazicGui;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.client.render.bars.DeafeningType;
import net.arkadiyhimself.fantazia.client.render.bars.DisarmedSwordType;
import net.arkadiyhimself.fantazia.client.render.bars.SnowCrystalType;
import net.arkadiyhimself.fantazia.client.render.bars.StunBarType;
import net.arkadiyhimself.fantazia.client.screen.TalentsScreen;
import net.arkadiyhimself.fantazia.items.casters.AuraCaster;
import net.arkadiyhimself.fantazia.items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBlade;
import net.arkadiyhimself.fantazia.items.weapons.Melee.Murasama;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.KeyInputC2S;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.tags.FTZSoundEventTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import org.joml.Quaternionf;

import java.util.List;
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Fantazia.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void fovModifier(ComputeFovModifierEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(FTZMobEffects.FURY.get())) event.setNewFovModifier(event.getNewFovModifier() * 1.1f);
    }
    @SubscribeEvent
    public static void renderGuiPre(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = event.getGuiGraphics().pose();
        NamedGuiOverlay overlay = event.getOverlay();

        FrozenEffect frozenEffect = EffectGetter.takeEffectHolder(player, FrozenEffect.class);
        if (frozenEffect != null) {
            float effPer = frozenEffect.effectPercent();
            float frePer = player.getPercentFrozen();
            if (effPer > frePer && overlay == VanillaGuiOverlay.FROSTBITE.type()) event.setCanceled(true);
        }

        if (overlay == VanillaGuiOverlay.SUBTITLES.type() && !player.shouldShowDeathScreen()) poseStack.translate(0,-24,0);
        if (overlay == VanillaGuiOverlay.EXPERIENCE_BAR.type()) {
            int x = event.getWindow().getGuiScaledWidth() / 2 - 91;
            int y = event.getWindow().getGuiScaledHeight() - 29;

            StunEffect stunEffect = EffectGetter.takeEffectHolder(player, StunEffect.class);
            BarrierEffect barrierEffect = EffectGetter.takeEffectHolder(player, BarrierEffect.class);

            poseStack.pushPose();
            if (FantazicGui.renderStunBar(stunEffect, guiGraphics, x, y)) event.setCanceled(true);
            else if (FantazicGui.renderBarrierBar(barrierEffect, guiGraphics, x, y)) event.setCanceled(true);
            poseStack.popPose();
        }
    }
    @SubscribeEvent
    public static void renderGuiPost(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = event.getGuiGraphics().pose();
        NamedGuiOverlay overlay = event.getOverlay();
        if (overlay == VanillaGuiOverlay.SUBTITLES.type() && !player.shouldShowDeathScreen()) poseStack.translate(0,24,0);
    }
    @SubscribeEvent
    public static void renderBackground(ScreenEvent.Render.BackgroundRendered event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof EffectRenderingInventoryScreen<?>)) return;
        FantazicGui.renderAurasInventory(event.getGuiGraphics());
    }
    @SubscribeEvent
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderLivingPre(RenderLivingEvent.Pre<T, M> event) {
        assert Minecraft.getInstance().player != null;
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        Quaternionf cameraOrientation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        MultiBufferSource buffers = event.getMultiBufferSource();
        LivingEntityRenderer<T, M> renderer = event.getRenderer();
        M model = renderer.getModel();

        if (!event.getEntity().canChangeDimensions()) return;
        if (entity instanceof Player player && (player.isSpectator() || player.isCreative())) return;
        if (!entity.getPassengers().isEmpty()) return;

        DarkFlameTicks darkFlameTicks = DataGetter.takeDataHolder(entity, DarkFlameTicks.class);
        int yOffset = -10;
        if (darkFlameTicks != null && darkFlameTicks.isBurning()) VisualHelper.renderAncientFlame(poseStack, entity, buffers);
        if (entity == Minecraft.getInstance().player) return;

        poseStack.pushPose();

        poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        poseStack.mulPose(cameraOrientation);

        final float globalScale = 0.0625F;
        poseStack.scale(-globalScale, -globalScale, -globalScale);

        StunEffect stunEffect = EffectGetter.takeEffectHolder(entity, StunEffect.class);
        if (stunEffect != null && stunEffect.renderBar()) {
            yOffset = -18;
            StunBarType.render(stunEffect, poseStack, buffers);
        }

        FrozenEffect frozenEffect = EffectGetter.takeEffectHolder(entity, FrozenEffect.class);
        DisarmEffect disarmEffect = EffectGetter.takeEffectHolder(entity, DisarmEffect.class);
        DeafenedEffect deafenedEffect = EffectGetter.takeEffectHolder(entity, DeafenedEffect.class);

        if (disarmEffect != null && disarmEffect.renderDisarm()) DisarmedSwordType.render(poseStack, buffers, yOffset - 10);
        else if (frozenEffect != null && frozenEffect.renderFreeze()) SnowCrystalType.render(frozenEffect, poseStack, buffers, yOffset);
        else if (deafenedEffect != null && deafenedEffect.renderDeaf()) DeafeningType.render(deafenedEffect, poseStack, buffers, yOffset);

        poseStack.popPose();
    }
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderLivingPost(RenderLivingEvent.Post<T,M> event) {
        T entity = (T) event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        LivingEntityRenderer<T, M> renderer = event.getRenderer();
        M model = renderer.getModel();
        int packedLight = event.getPackedLight();
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0);

        EvasionData evasionData = DataGetter.takeDataHolder(event.getEntity(), EvasionData.class);
        if (evasionData != null && evasionData.getIFrames() > 0) VisualHelper.renderBlinkingEntity(entity, renderer, poseStack, buffers, packedLight, packedOverlay);
    }
    @SubscribeEvent
    public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().level != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            StunEffect stunEffect = EffectGetter.takeEffectHolder(player, StunEffect.class);
            if (stunEffect != null && stunEffect.stunned()) event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void mouseInputs(InputEvent.InteractionKeyMappingTriggered event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
        if (player.hasEffect(FTZMobEffects.DISARM.get())) event.setSwingHand(false);
    }
    @SubscribeEvent
    public static void keyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) return;

        if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue()) NetworkHandler.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.JUMP, event.getAction()));
    }
    @SubscribeEvent
    public static void input(InputEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) return;

        if (KeyBinding.BLOCK.consumeClick() && player.getMainHandItem().is(FTZItemTags.MELEE_BLOCK)) NetworkHandler.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.BLOCK, 1));
        if (KeyBinding.DASH.consumeClick()) NetworkHandler.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.DASH, 1));
        if (KeyBinding.SWORD_ABILITY.consumeClick()) NetworkHandler.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.WEAPON_ABILITY, 1));
        if (KeyBinding.SPELLCAST1.consumeClick()) NetworkHandler.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.SPELLCAST1, 1));
        if (KeyBinding.SPELLCAST2.consumeClick()) NetworkHandler.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.SPELLCAST2, 1));

        TalentsHolder talentsHolder = AbilityGetter.takeAbilityHolder(player, TalentsHolder.class);
        if (KeyBinding.TALENTS.consumeClick() && talentsHolder != null) Minecraft.getInstance().setScreen(new TalentsScreen(talentsHolder));
    }

    // the event is used to remove vanilla's "Attack damage: ..." and "Attack speed: ..." lines
    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        if (stack.getItem() instanceof SpellCaster spellCaster) tooltip.addAll(spellCaster.buildTooltip());
        if (stack.getItem() instanceof AuraCaster auraCaster) tooltip.addAll(auraCaster.buildTooltip());
        if (stack.getItem() instanceof FragileBlade fragileBlade) {
            Component name = event.getToolTip().get(0).copy();
            tooltip.clear();
            tooltip.add(GuiHelper.bakeComponent(name.getString(), null, null));
            tooltip.add(Component.translatable(" "));
            tooltip.addAll(fragileBlade.buildItemTooltip(stack));
        }
        if (stack.getItem() instanceof Murasama murasama) {
            Component name = event.getToolTip().get(0).copy();
            tooltip.clear();
            tooltip.add(GuiHelper.bakeComponent(name.getString(), null, null));
            tooltip.add(Component.translatable(" "));
            tooltip.addAll(murasama.buildItemTooltip(stack));
        }
    }
    @SubscribeEvent
    public static void playSound(PlaySoundEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(FTZMobEffects.DEAFENED.get())) {
            boolean cancel = true;
            ITagManager<SoundEvent> tagManager = ForgeRegistries.SOUND_EVENTS.tags();
            if (tagManager == null) return;
            List<SoundEvent> soundEvents = tagManager.getTag(FTZSoundEventTags.NOT_MUTED).stream().toList();
            for (SoundEvent soundEvent : soundEvents) if (event.getOriginalSound().getLocation().equals(soundEvent.getLocation())) cancel = false;
            if (cancel) event.setSound(null);
        }
    }
    @SubscribeEvent
    public static void onRenderFire(RenderBlockScreenEffectEvent event) {
        DarkFlameTicks darkFlameTicks = DataGetter.takeDataHolder(Minecraft.getInstance().player, DarkFlameTicks.class);
        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE && darkFlameTicks != null && darkFlameTicks.isBurning()) event.setCanceled(true);
    }

}
