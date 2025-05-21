package net.arkadiyhimself.fantazia.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.FantazicConfig;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.KeyBinding;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.CurrentAndInitialValue;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DoubleJumpHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.MeleeBlockHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.client.gui.FantazicClientBossEvent;
import net.arkadiyhimself.fantazia.client.gui.FantazicGui;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.client.render.bars.*;
import net.arkadiyhimself.fantazia.client.render.layers.BarrierLayer;
import net.arkadiyhimself.fantazia.client.screen.TalentScreen;
import net.arkadiyhimself.fantazia.items.RuneWielderItem;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.MeleeWeaponItem;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.packets.stuff.KeyInputC2S;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.tags.FTZSoundEventTags;
import net.arkadiyhimself.fantazia.util.library.Vector3;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, modid = Fantazia.MODID)
public class ClientEvents {

    public static @Nullable LivingEntity suitableTarget = null;
    public static @Nullable AuraCasterItem heldAuraCaster = null;
    public static int lastWisdom = 0;
    public static int wisdomTick = 0;

    @SubscribeEvent
    public static void entityTickPre(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) return;
        if (entity == suitableTarget && Screen.hasShiftDown()) VisualHelper.particleOnEntityClient(entity, ParticleTypes.SMOKE, ParticleMovement.REGULAR);
    }

    @SubscribeEvent
    public static void fovModifier(ComputeFovModifierEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(FTZMobEffects.FURY)) event.setNewFovModifier(event.getNewFovModifier() * 1.1f);
    }

    @SubscribeEvent
    public static void renderGuiPre(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = event.getGuiGraphics().pose();
        ResourceLocation overlay = event.getName();

        if (overlay.equals(VanillaGuiLayers.SUBTITLE_OVERLAY) && !player.shouldShowDeathScreen()) poseStack.translate(0,-64 - FantazicConfig.staminaBarYoff.get(),0);

        if (overlay.equals(VanillaGuiLayers.EXPERIENCE_BAR) || overlay.equals(VanillaGuiLayers.EXPERIENCE_LEVEL)) {
            if (player.hasEffect(FTZMobEffects.STUN) || player.hasEffect(FTZMobEffects.BARRIER) || player.hasEffect(FTZMobEffects.LAYERED_BARRIER)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderGuiPost(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        PoseStack poseStack = event.getGuiGraphics().pose();
        ResourceLocation overlay = event.getName();
        if (overlay.equals(VanillaGuiLayers.SUBTITLE_OVERLAY) && !player.shouldShowDeathScreen()) poseStack.translate(0,64 + FantazicConfig.staminaBarYoff.get(),0);
    }

    @SubscribeEvent
    public static void renderBackground(ContainerScreenEvent.Render.Background event) {
        Screen screen = event.getContainerScreen();
        if (!(screen instanceof EffectRenderingInventoryScreen<?>)) return;
        FantazicGui.renderAurasInventory(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void renderPlayerPre(RenderPlayerEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        Quaternionf cameraOrientation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        MultiBufferSource buffers = event.getMultiBufferSource();

        if (entity instanceof Player player && player.isSpectator()) return;
        if (!entity.getPassengers().isEmpty()) return;

        float yOffset = -1f;
        if (entity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() > 0) VisualHelper.renderAncientFlame(poseStack, entity, buffers);
        if (entity == Minecraft.getInstance().player) return;

        poseStack.pushPose();

        poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        poseStack.mulPose(cameraOrientation);

        poseStack.scale(1,-1,1);

        StunEffectHolder stunEffect = LivingEffectHelper.takeHolder(entity, StunEffectHolder.class);
        if (stunEffect != null && stunEffect.renderBar()) {
            yOffset = -1.45f;
            StunBarType.render(stunEffect, poseStack, buffers);
        }

        CurrentAndInitialValue frozenDuration = LivingEffectHelper.getDurationHolder(entity, FTZMobEffects.FROZEN.value());

        if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.CURSED_MARK.value())) CursedMarkType.render(poseStack, buffers, yOffset);
        else if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.DISARM.value())) DisarmedSwordType.render(poseStack, buffers, yOffset);
        else if (frozenDuration != null && (frozenDuration.value() > 0 || entity.getPercentFrozen() > 0)) SnowCrystalType.render(entity, frozenDuration, poseStack, buffers, yOffset);
        else if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.DEAFENED.value())) DeafeningType.render(entity, poseStack, buffers, yOffset);

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        PlayerRenderer renderer = event.getRenderer();
        int packedLight = event.getPackedLight();
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(player, 0);

        EvasionHolder evasionHolder = LivingDataHelper.takeHolder(player, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) VisualHelper.renderEvasionPlayer(player, renderer, poseStack, buffers, packedLight, packedOverlay);
    }

    @SubscribeEvent
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderLivingPre(RenderLivingEvent.Pre<T, M> event) {
        assert Minecraft.getInstance().player != null;
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        Quaternionf cameraOrientation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        MultiBufferSource buffers = event.getMultiBufferSource();

        if (entity instanceof Player player && player.isSpectator()) return;
        if (!entity.getPassengers().isEmpty()) return;

        float yOffset = -1f;
        if (entity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() > 0) VisualHelper.renderAncientFlame(poseStack, entity, buffers);
        if (entity == Minecraft.getInstance().player) return;

        poseStack.pushPose();

        poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        poseStack.mulPose(cameraOrientation);

        poseStack.scale(1,-1,1);

        StunEffectHolder stunEffect = LivingEffectHelper.takeHolder(entity, StunEffectHolder.class);
        if (stunEffect != null && stunEffect.renderBar()) {
            yOffset = -1.45f;
            StunBarType.render(stunEffect, poseStack, buffers);
        }

        CurrentAndInitialValue frozenDuration = LivingEffectHelper.getDurationHolder(entity, FTZMobEffects.FROZEN.value());

        if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.CURSED_MARK.value())) CursedMarkType.render(poseStack, buffers, yOffset);
        else if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.DISARM.value())) DisarmedSwordType.render(poseStack, buffers, yOffset);
        else if (frozenDuration != null && (frozenDuration.value() > 0 || entity.getPercentFrozen() > 0)) SnowCrystalType.render(entity, frozenDuration, poseStack, buffers, yOffset);
        else if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.DEAFENED.value())) DeafeningType.render(entity, poseStack, buffers, yOffset);

        poseStack.popPose();
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderLivingPost(RenderLivingEvent.Post<T,M> event) {
        T entity = (T) event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        LivingEntityRenderer<T, M> renderer = event.getRenderer();
        int packedLight = event.getPackedLight();
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0);

        EvasionHolder evasionHolder = LivingDataHelper.takeHolder(entity, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) VisualHelper.renderEvasionEntity(entity, renderer, poseStack, buffers, packedLight, packedOverlay);
    }

    @SubscribeEvent
    public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void interactionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
        if (player.hasEffect(FTZMobEffects.DISARM)) event.setSwingHand(false);
        if (KeyBinding.BLOCK.isDown() && player.getMainHandItem().is(FTZItemTags.MELEE_BLOCK) && player.getOffhandItem().isEmpty()) PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, MeleeBlockHolder::startBlocking);
    }

    @SubscribeEvent
    public static void keyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || ActionsHelper.preventActions(player) || player.isSpectator()) return;
        int action = event.getAction();

        if (KeyBinding.SWORD_ABILITY.consumeClick()) IPacket.keyInput(KeyInputC2S.INPUT.WEAPON_ABILITY, action);
        if (KeyBinding.SPELLCAST1.consumeClick()) IPacket.keyInput(KeyInputC2S.INPUT.SPELLCAST1, action);
        if (KeyBinding.SPELLCAST2.consumeClick()) IPacket.keyInput(KeyInputC2S.INPUT.SPELLCAST2, action);
        if (KeyBinding.SPELLCAST3.consumeClick()) IPacket.keyInput(KeyInputC2S.INPUT.SPELLCAST3, action);

        TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(player, TalentsHolder.class);
        if (KeyBinding.TALENTS.isDown() && talentsHolder != null) Minecraft.getInstance().setScreen(new TalentScreen(talentsHolder));

        if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue()) {
            if (action == 1) PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::tryToJumpClient);
            else if (action == 0)  PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::buttonRelease);
        }

        if (KeyBinding.DASH.isDown()) PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::beginDash);
    }

    // the event is used to remove vanilla's "Attack damage: ..." and "Attack speed: ..." lines
    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        Item item = stack.getItem();
        if (item instanceof SpellCasterItem spellCasterItem) tooltip.addAll(spellCasterItem.buildTooltip());
        if (item instanceof AuraCasterItem auraCasterItem) tooltip.addAll(auraCasterItem.buildTooltip());
        if (item instanceof MeleeWeaponItem meleeWeaponItem) {
            Component name = event.getToolTip().getFirst().copy();
            tooltip.clear();
            tooltip.add(GuiHelper.bakeComponent(name.getString(), null, null));
            tooltip.add(Component.literal(" "));
            tooltip.addAll(meleeWeaponItem.itemTooltip(stack));
        } else if (item instanceof RuneWielderItem runeWielderItem) {
            Component name = event.getToolTip().getFirst().copy();
            tooltip.clear();
            tooltip.add(GuiHelper.bakeComponent(name.getString(), null, null));
            tooltip.add(Component.literal(" "));
            tooltip.addAll(runeWielderItem.itemTooltip(stack));
        }
    }

    @SubscribeEvent
    public static void playSound(PlaySoundEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(FTZMobEffects.DEAFENED)) {
            Optional<Holder.Reference<SoundEvent>> soundEvent = BuiltInRegistries.SOUND_EVENT.getHolder(event.getOriginalSound().getLocation());
            if (soundEvent.isEmpty()) return;
            if (!FTZSoundEventTags.hasTag(soundEvent.get(), FTZSoundEventTags.NOT_MUTED)) event.setSound(null);
        }
    }

    @SubscribeEvent
    public static void renderBlockScreenEffect(RenderBlockScreenEffectEvent event) {
        if (Minecraft.getInstance().player == null) return;
        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE && Minecraft.getInstance().player.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() > 0) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void clientTickPre(ClientTickEvent.Pre event) {
        VisualHelper.wanderersSpiritParticles();
        if (wisdomTick > 0) wisdomTick--;

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;

        BlockPos playerPos = localPlayer.blockPosition();
        int x0 = playerPos.getX();
        int y0 = playerPos.getY();
        int z0 = playerPos.getZ();

        Item item = localPlayer.getMainHandItem().getItem();

        if ((localPlayer.tickCount % 6) == 0 && item instanceof AuraCasterItem auraCasterItem && Screen.hasShiftDown()) {
            heldAuraCaster = auraCasterItem;
            float radius = heldAuraCaster.getAura().value().getRadius();

            AttributeInstance instance = localPlayer.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
            float additional = instance == null ? 0 : (float) instance.getValue();

            int finalRadius = (int) (radius + Math.max(0, additional)) + 1;
            Minecraft.getInstance().levelRenderer.setBlocksDirty(x0 - finalRadius, y0 - finalRadius, z0 - finalRadius, x0 + finalRadius, y0 + finalRadius, z0 + finalRadius);
        } else if ((!(item instanceof AuraCasterItem) || !Screen.hasShiftDown()) && heldAuraCaster != null) {
            float radius = heldAuraCaster.getAura().value().getRadius();
            heldAuraCaster = null;

            AttributeInstance instance = localPlayer.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
            float additional = instance == null ? 0 : (float) instance.getValue();

            int finalRadius = (int) (radius + Math.max(0, additional)) + 1;
            Minecraft.getInstance().levelRenderer.setBlocksDirty(x0 - finalRadius, y0 - finalRadius, z0 - finalRadius, x0 + finalRadius, y0 + finalRadius, z0 + finalRadius);
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (item instanceof SpellCasterItem spellCasterItem && spellCasterItem.getSpell().value() instanceof TargetedSpell<?> targetedSpell && level != null) {
            float range = targetedSpell.range();

            AttributeInstance instance = localPlayer.getAttribute(FTZAttributes.CAST_RANGE_ADDITION);
            float additional = instance == null ? 0 : (float) instance.getValue();

            int finalRange = (int) (range + additional);

            Vector3 casterCenter = Vector3.fromEntityCenter(localPlayer);
            Vector3 head;

            for (float i = 1; i < finalRange; i += 0.5f) {
                head = casterCenter.add(new Vector3(localPlayer.getLookAngle().normalize()).multiply(i)).add(0.0, 0.5, 0.0);
                BlockPos blockPos = new BlockPos((int) head.x, (int) head.y, (int) head.z);

                AABB aabb = FantazicMath.squareBoxFromCenterAndSide(head.toVec3D(), SpellHelper.RAY_RADIUS);
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb);
                list.removeIf(livingEntity -> livingEntity == localPlayer
                        || !localPlayer.hasLineOfSight(livingEntity) || livingEntity.isInvisible() ||
                        !targetedSpell.canAffect(livingEntity) || !targetedSpell.conditions(localPlayer, livingEntity)
                || livingEntity.isInvisible());
                if (!list.isEmpty()) {
                    LivingEntity livingEntity = SpellHelper.getClosestEntity(list, localPlayer);
                    if (livingEntity != null) {
                        suitableTarget = livingEntity;
                        break;
                    }
                }

                if (level.getBlockState(blockPos).isSolid()) {
                    float j = i - 0.75f;
                    head = casterCenter.add(new Vector3(localPlayer.getLookAngle().normalize()).multiply(j)).add(0.0, 0.5, 0.0);
                    if (Screen.hasShiftDown()) level.addParticle(ParticleTypes.SMOKE, head.x, head.y, head.z,0,0,0);
                    suitableTarget = null;
                    break;
                }

                if (Math.abs(i - finalRange) <= 0.5f) {
                    if (Screen.hasShiftDown()) level.addParticle(ParticleTypes.SMOKE, head.x, head.y, head.z,0,0,0);
                    suitableTarget = null;
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void clientTickPost(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Item item = player.getMainHandItem().getItem();
            if (!(item instanceof SpellCasterItem spellCasterItem) || !(spellCasterItem.getSpell().value() instanceof TargetedSpell<?>)) suitableTarget = null;
        }
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        MeleeBlockHolder holder = PlayerAbilityHelper.takeHolder(Minecraft.getInstance().player, MeleeBlockHolder.class);
        PoseStack poseStack = event.getPoseStack();
        if (holder == null) return;
        int anim = holder.anim();
        if (anim <= 0) return;

        float delta;
        if (anim < 6) delta = (anim - event.getPartialTick()) / 6f;
        else if (anim > MeleeBlockHolder.BLOCK_ANIM - 3f) delta = (MeleeBlockHolder.BLOCK_ANIM - anim + event.getPartialTick()) / 3;
        else delta = 1f;

        int multip = Minecraft.getInstance().options.mainHand().get() == HumanoidArm.RIGHT ? 1 : -1;

        if (event.getHand() == InteractionHand.MAIN_HAND) {
            poseStack.translate(0.55f * multip * delta, 0.35f * delta, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(90f * multip * delta));
            poseStack.mulPose(Axis.XP.rotationDegrees(-45f * delta));
        } else {
            poseStack.translate(-0.55 * multip * delta, -0.35 * delta, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90 * multip * delta));
            poseStack.mulPose(Axis.XP.rotationDegrees(45f * delta));
        }
    }

    @SubscribeEvent
    public static void bossEventProgress(CustomizeGuiOverlayEvent.BossEventProgress event) {
        LocalPlayer player = Minecraft.getInstance().player;
        LerpingBossEvent lerpingBossEvent = event.getBossEvent();
        if (!(lerpingBossEvent instanceof FantazicClientBossEvent bossEvent) || player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();
        float barr = bossEvent.getBarrier();
        int barrier = Mth.lerpDiscrete(barr, 0, 180);

        int x = event.getX();
        int y = event.getY();
        int tick = player.tickCount % 64;
        float[] color = RenderSystem.getShaderColor().clone();
        guiGraphics.setColor(0.7f,0.7f,0.7f,0.25f);

        poseStack.pushPose();
        poseStack.translate(0,0,200);
        guiGraphics.blit(BarrierLayer.BARRIER_BG, x + 1, y + 1, 0,0, barrier, 3, 32, 32);
        guiGraphics.blit(BarrierLayer.BARRIER_LAYER, x + 1, y + 1, tick, tick, barrier, 3, 64, 64);
        guiGraphics.setColor(color[0], color[1], color[2], color[3]);
        poseStack.popPose();
    }
}
