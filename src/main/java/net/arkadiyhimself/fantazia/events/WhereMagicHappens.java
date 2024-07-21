package net.arkadiyhimself.fantazia.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.arkadiyhimself.fantazia.advanced.capacity.SpellHandler.Spell;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.entities.HatchetEntity;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.Items.casters.DashStone;
import net.arkadiyhimself.fantazia.Items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.capabilityupdate.EntityMadeSoundS2C;
import net.arkadiyhimself.fantazia.networking.packets.KickOutOfGuiS2CPacket;
import net.arkadiyhimself.fantazia.registry.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.registry.ItemRegistry;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.Abilities.AttackBlock;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.Abilities.Dash;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.Abilities.RenderingValues;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData.CommonData;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.BarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.StunEffect;
import net.arkadiyhimself.fantazia.advanced.capability.level.LevelCap;
import net.arkadiyhimself.fantazia.advanced.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.util.library.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class WhereMagicHappens {
    public static class Gui {
        public static void renderOnTheWholeScreen(ResourceLocation resourceLocation, float red, float green, float blue, float alpha) {
            int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            float[] previousSC = RenderSystem.getShaderColor();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(red, green, blue, alpha);
            RenderSystem.setShaderTexture(0, resourceLocation);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, height, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(width, height, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(width, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(previousSC[0], previousSC[1], previousSC[2], previousSC[3]);
        }
        public static<T extends Item> List<ItemStack> searchForItems(Player player, Class<T> tClass) {
            List<ItemStack> itemStacks = new ArrayList<>();

            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem().getClass() == tClass) {
                    itemStacks.add(stack);
                }
            }
            for (ItemStack stack : player.getInventory().offhand) {
                if (stack.getItem().getClass() == tClass) {
                    itemStacks.add(stack);
                }
            }
            return itemStacks;
        }
        public static List<ItemStack> searchForItems(Player player, Item... items) {
            List<ItemStack> itemStacks = new ArrayList<>();

            for (ItemStack stack : player.getInventory().items) {
                if (Arrays.stream(items).anyMatch((Predicate<? super Item>) stack.getItem())) {
                    itemStacks.add(stack);
                }
            }
            for (ItemStack stack : player.getInventory().offhand) {
                if (Arrays.stream(items).anyMatch((Predicate<? super Item>) stack.getItem())) {
                    itemStacks.add(stack);
                }
            }
            return itemStacks;
        }
        public static void addComponent(List<Component> list, String str, @Nullable ChatFormatting[] strFormat, @Nullable ChatFormatting[] varFormat, Object... objs) {
            Component[] stringValues = new Component[objs.length];
            int counter = 0;
            for (Object obj : objs) {
                MutableComponent comp;

                if (obj instanceof MutableComponent mut) {
                    comp = mut;
                } else {
                    comp = Component.literal(obj.toString());
                }

                if (varFormat != null) {
                    comp = comp.withStyle(varFormat);
                }

                stringValues[counter] = comp;
                counter++;
            }
            if (strFormat == null) {
                list.add(Component.translatable(str, stringValues));
            } else {
                list.add(Component.translatable(str, stringValues).withStyle(strFormat));
            }
        }
    }
    public static class Abilities {
        public static List<ResourceKey<DamageType>> notGlowRed = new ArrayList<>() {{
            add(DamageTypeRegistry.BLEEDING);
        }};
        public static HashMap<LivingEntity, ItemStack> hatchetStuck = new HashMap<>();


        // finds out whether an entity has a spell at all, doesn't matter whether it's on cooldown or not
        public static boolean hasSpell(LivingEntity entity, Spell spell) {
            List<SlotResult> slotResults = Lists.newArrayList();
            slotResults.addAll(findCurios(entity, "passivecaster"));
            slotResults.addAll(findCurios(entity, "spellcaster"));
            boolean flag = false;
            for (SlotResult slotResult : slotResults) {
                if (slotResult.stack().getItem() instanceof SpellCaster spellCaster && spellCaster.getSpell() == spell) {
                    flag = true;
                }
            }
            return flag;
        }

        // finds out whether an entity has a spell and doesn't have a cooldown on it, and if it does, the item is put on cooldown and it returns true
        public static boolean hasActiveSpell(LivingEntity entity, Spell spell) {
            List<SlotResult> slotResults = Lists.newArrayList();
            slotResults.addAll(findCurios(entity, "passivecaster"));
            slotResults.addAll(findCurios(entity, "spellcaster"));
            boolean flag = false;
            for (SlotResult slotResult : slotResults) {
                if (slotResult.stack().getItem() instanceof SpellCaster spellCaster && spellCaster.getSpell() == spell) {
                    if (!(entity instanceof ServerPlayer serverPlayer)) return true;
                    else {
                        if (!serverPlayer.getCooldowns().isOnCooldown(spellCaster)) {
                            flag = true;
                            serverPlayer.getCooldowns().addCooldown(spellCaster, spell.getRecharge());
                        }
                    }
                }
            }
            return flag;
        }
        public static boolean isInvulnerable(LivingEntity entity) {
            if (entity instanceof Player player) {
                AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                if (abilityManager != null) {
                    Dash dash = abilityManager.takeAbility(Dash.class);
                    if (dash != null && dash.isDashing() && dash.getLevel() >= 2) return true;
                }
            }
            return entity.isInvulnerable() || entity.hurtTime > 0;
        }
        public static void listenVibration(ServerLevel pLevel, GameEvent.Context pContext, Vec3 pPos, ServerPlayer player) {
            if (!hasCurio(player, ItemRegistry.SCULK_HEART.get()) || pContext.sourceEntity() == null || !(pContext.sourceEntity() instanceof LivingEntity livingEntity))
                return;
            Vec3 vec3 = player.getPosition(1f);
            if (shouldPlayerListen(pLevel, BlockPos.containing(pPos), pContext, player) && !isOccluded(pLevel, pPos, vec3)) {
                AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                if (abilityManager == null) return;
                abilityManager.getAbility(RenderingValues.class).ifPresent(renderingValues -> {
                    if (renderingValues.vibr_cd == 0) {
                        renderingValues.vibr_cd = 40;
                        renderingValues.madeSound(livingEntity);
                        NetworkHandler.sendToPlayer(new EntityMadeSoundS2C(livingEntity), player);
                        PositionSource listenerSource = new EntityPositionSource(player, 1.5f);
                        int travelTimeInTicks = Mth.floor(player.distanceToSqr(pPos)) / 4;
                        pLevel.sendParticles(new VibrationParticleOption(listenerSource, travelTimeInTicks), pPos.x, pPos.y, pPos.z, 3, 0.0D, 0.0D, 0.0D, 0.0D);
                        pLevel.playSound(null, pPos.x() + 0.5D, pPos.y() + 0.5D, pPos.z() + 0.5D, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, pLevel.random.nextFloat() * 0.2F + 0.8F);
                    }
                });
            }
        }

        public static boolean shouldPlayerListen(ServerLevel pLevel, BlockPos pPos, GameEvent.Context pContext, LivingEntity entity) {
            if (pContext.sourceEntity() != null && pContext.sourceEntity().isCrouching() || pContext.sourceEntity() == entity) {
                return false;
            }
            return !entity.isDeadOrDying() && pLevel.getWorldBorder().isWithinBounds(pPos) && !entity.hasEffect(MobEffectRegistry.DEAFENED.get());
        }

        private static boolean isOccluded(Level pLevel, Vec3 pFrom, Vec3 pTo) {
            Vec3 vec3 = new Vec3((double) Mth.floor(pFrom.x) + 0.5D, (double) Mth.floor(pFrom.y) + 0.5D, (double) Mth.floor(pFrom.z) + 0.5D);
            Vec3 vec31 = new Vec3((double) Mth.floor(pTo.x) + 0.5D, (double) Mth.floor(pTo.y) + 0.5D, (double) Mth.floor(pTo.z) + 0.5D);

            for (Direction direction : Direction.values()) {
                Vec3 vec32 = vec3.relative(direction, 1.0E-5F);
                if (pLevel.isBlockInLine(new ClipBlockStateContext(vec32, vec31, (p_223780_) -> p_223780_
                        .is(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() != HitResult.Type.BLOCK) {
                    return false;
                }
            }
            return true;
        }

        private static void dropXPOrb(Level level, double x, double y, double z, int xp) {
            ExperienceOrb orb = new ExperienceOrb(level, x, y, z, xp);
            level.addFreshEntity(orb);
        }

        public static boolean cancelMouseMoving(LocalPlayer player) {
            if (player == null) return false;

            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                AttackBlock blocking = abilityManager.takeAbility(AttackBlock.class);
                Dash dash = abilityManager.takeAbility(Dash.class);
                if (blocking != null && blocking.isInAnim()) return true;
                if (dash != null && dash.isDashing()) return true;

            }

            EffectManager effectManager = EffectGetter.getUnwrap(player);
            if (effectManager != null) {
                StunEffect stun = effectManager.takeEffect(StunEffect.class);
                if (stun != null && stun.stunned()) return true;
            }

            return false;
        }
        public static <T extends ParticleOptions> void rayOfParticles(LivingEntity caster, LivingEntity target, T type) {
            Vec3 vec3 = caster.position().add(0.0D, 1.2F, 0.0D);
            Vec3 vec31 = target.getEyePosition().subtract(vec3);
            Vec3 vec32 = vec31.normalize();

            for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
                Vec3 vec33 = vec3.add(vec32.scale(i));
                if (caster.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(type, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        public static void dropExperience(LivingEntity entity, float multiplier) {
            if (entity.level() instanceof ServerLevel) {
                int reward = (int) (entity.getExperienceReward() * multiplier);
                ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), reward);
            }
        }

        public static void addEffectWithoutParticles(LivingEntity entity, MobEffect effect, int duration, int level) {
            entity.addEffect(new MobEffectInstance(effect, duration, level, true, false, true));
        }

        public static void addEffectWithoutParticles(LivingEntity entity, MobEffect effect, int duration) {
            addEffectWithoutParticles(entity, effect, duration, 0);
        }

        public static boolean canNotDoActions(Player player) {
            if (player == null) return true;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                AttackBlock attackBlock = abilityManager.takeAbility(AttackBlock.class);
                if (attackBlock != null && attackBlock.isInAnim()) return true;

                Dash dash = abilityManager.takeAbility(Dash.class);
                if (dash != null && dash.isDashing()) return true;
            }

            EffectManager effectManager = EffectGetter.getUnwrap(player);
            if (effectManager != null) {
                StunEffect stunEffect = effectManager.takeEffect(StunEffect.class);
                if (stunEffect != null && stunEffect.stunned()) return true;
            }

            return false;
        }
        public static boolean isUnderAura(Entity entity, BasicAura<?,?> aura) {
            CommonData data = AttachCommonData.getUnwrap(entity);
            if (data == null) {
                return false;
            } else {
                boolean flag = false;
                for (AuraInstance<?,?> instance : WhereMagicHappens.Abilities.getAffectingAuras(entity)) {
                    if (instance.getAura() == aura) {
                        flag = true;
                        break;
                    }
                }
                return flag;
            }
        }
        public static void interrupt(LivingEntity entity) {
            if (entity instanceof ServerPlayer player) {
                NetworkHandler.sendToPlayer(new KickOutOfGuiS2CPacket(), player);
                player.stopUsingItem();
                player.stopSleeping();
            } else if (entity instanceof Mob mob) {
                mob.setTarget(null);
                if (mob instanceof Warden warden) {
                    warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    warden.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
                }
            }
        }

        public static boolean blocksDamage(LivingEntity entity) {
            if (entity instanceof Player player) {
                AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                if (abilityManager != null) {
                    Dash dash = abilityManager.takeAbility(Dash.class);
                    if (dash != null && dash.isDashing() && dash.getLevel() > 1) return true;
                }
            }
            EffectManager effectManager = EffectGetter.getUnwrap(entity);
            if (effectManager != null) {
                BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
                if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

                LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
                if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

                AbsoluteBarrierEffect absoluteBarrierEffect = effectManager.takeEffect(AbsoluteBarrierEffect.class);
                if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;
            }
            return false;
        }

        public static boolean hasDashstone(LivingEntity entity) {
            AtomicBoolean present = new AtomicBoolean(false);
            CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
                List<SlotResult> slotResults = inventory.findCurios("dashstone");
                if (!slotResults.isEmpty() && !(slotResults.get(0).stack().getItem() instanceof DashStone)) {
                    present.set(true);
                }
            });
            return present.get();
        }

        public static boolean hasActiveCurio(final Player player, final Item curio) {
            return hasCurio(player, curio) && player.getCooldowns().isOnCooldown(curio);
        }

        // sorts a list of aura instances with a complicated algorithm, removing an aura instance if entity doesn't Primary Conditions and then prioritising instances where entity matches Secondary Conditions
        public static <T extends Entity, M extends Entity> List<AuraInstance<T, M>> getUniqueAuras(List<AuraInstance<T, M>> instances, @NotNull T entity) {
            instances.removeIf(auraInstance -> !auraInstance.isInside(entity));
            instances.removeIf(auraInstance -> !auraInstance.getAura().getAffectedType().isInstance(entity) && !Fantazia.DEVELOPER_MODE);
            instances.removeIf(auraInstance -> !auraInstance.getAura().primaryFilter.test(entity, auraInstance.getOwner()) && !Fantazia.DEVELOPER_MODE);
            List<AuraInstance<T, M>> unique = Lists.newArrayList();
            while (!instances.isEmpty()) {
                AuraInstance<T, M> instance = instances.get(0);
                AuraInstance<T, M> busyInstance = null;
                boolean hasSameAura = false;
                for (int i = 0; i < unique.size(); i++) {
                    if (unique.get(i).getAura() == instance.getAura()) {
                        hasSameAura = true;
                        busyInstance = unique.get(i);
                        break;
                    }
                }
                if (hasSameAura) {
                    boolean secCond = instance.getAura().secondaryFilter.test(instance.getAura().getAffectedType().cast(entity), instance.getOwner());
                    boolean secCondBusy = busyInstance.getAura().secondaryFilter.test(instance.getAura().getAffectedType().cast(entity), instance.getOwner());
                    if (!secCondBusy && secCond) {
                        unique.remove(busyInstance);
                        unique.add(instance);
                    }
                } else {
                    unique.add(instance);
                }
                instances.remove(instance);
            }
            return unique;
        }
        public static <T extends Entity, M extends Entity> List<AuraInstance<T, M>> getAffectingAuras(@NotNull T entity) {
            LevelCap levelCap = LevelCapGetter.getLevelCap(entity.level());
            List<AuraInstance<T, M>> auras = Lists.newArrayList();
            if (levelCap == null) return auras;
            levelCap.getAuraInstances().forEach(auraInstance -> {
                if (auraInstance.getAura().getAffectedType().isInstance(entity)) {
                    auras.add((AuraInstance<T, M>) auraInstance);
                }
            });
            return getUniqueAuras(auras, entity);
        }
        public static boolean hasCurio(final LivingEntity entity, final Item curio) {
            AtomicBoolean present = new AtomicBoolean(false);
            CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
                List<SlotResult> slots = inventory.findCurios(curio);
                if (!slots.isEmpty()) present.set(true);
            });
            return present.get();
        }
        public static int getDuplicatingCurios(final LivingEntity entity, final Item item) {
            AtomicInteger present = new AtomicInteger(0);
            CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
                List<SlotResult> slots = inventory.findCurios(item);
                present.set(slots.size());
            });
            return present.get();
        }
        public static List<SlotResult> findAllCurio(LivingEntity entity, String id) {
            List<SlotResult> result = new ArrayList<>();
            if (CuriosApi.getCuriosInventory(entity).isPresent()) {
                ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
                result = handler.findCurios(id);
            }
            return result;
        }

        public static List<SlotResult> findCurios(LivingEntity entity, String ident) {
            List<SlotResult> result = new ArrayList<>();
            if (CuriosApi.getCuriosInventory(entity).isPresent()) {
                ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
                Map<String, ICurioStacksHandler> curios = handler.getCurios();
                for (String id : curios.keySet()) {

                    if (id.contains(ident)) {
                        ICurioStacksHandler stacksHandler = curios.get(id);
                        IDynamicStackHandler stackHandler = stacksHandler.getStacks();

                        for (int i = 0; i < stackHandler.getSlots(); i++) {
                            ItemStack stack = stackHandler.getStackInSlot(i);

                            NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                            result.add(new SlotResult(new SlotContext(id, handler.getWearer(), i, false,
                                    renderStates.size() > i && renderStates.get(i)), stack));
                        }
                    }
                }
            }
            return result;
        }
        public static Vec3 calculateDashHorizontalVelocity(Vec3 angle, double velocity, boolean horizontal) {
            if (horizontal) angle = new Vec3(angle.x(),0, angle.z());
            return angle.normalize().scale(velocity);
        }
        public static Vec3 calculateDashHorizontalVelocity(LivingEntity entity, double velocity, boolean horizontal) {
            return calculateDashHorizontalVelocity(entity.getLookAngle(), velocity, horizontal);
        }
        public static int thirdLevelDashDuration(ServerPlayer player, Vec3 vec3, int initDuration) {
            double x0 = player.getX();
            double z0 = player.getZ();
            float multiplier = 2.4f;

            int m = 0;
            for (int i = initDuration; i > 0; i--) {
                Vec3 finalPos = new Vec3(x0 + vec3.x() / multiplier * i, player.getY() + 1 + vec3.y() / multiplier * i, z0 + vec3.z() / multiplier * i);
                if (player.level().getBlockState(BlockPos.containing(finalPos)).getBlock() instanceof AirBlock) {
                    m = i;
                    break;
                }
            }
            return m;
        }
        public static void animatePlayer(AbstractClientPlayer player, @Nullable String name) {
            ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player)
                    .get(Fantazia.res("animation"));
            if (animation != null) {
                if (name != null) {
                    @Nullable KeyframeAnimation keyframeAnimation = PlayerAnimationRegistry.getAnimation(Fantazia.res(name));
                    if (keyframeAnimation != null) {
                        animation.setAnimation(new KeyframeAnimationPlayer(keyframeAnimation));
                    }
                } else {
                    animation.setAnimation(null);
                }
            }
        }

        public static void animatePlayer(AbstractClientPlayer player, @Nullable IAnimation animation) {
            ModifierLayer<IAnimation> animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player)
                    .get(Fantazia.res("animation"));
            if (animationLayer != null) {
                if (animation != null) {
                    animationLayer.setAnimation(animation);
                } else {
                    animationLayer.setAnimation(null);
                }
            }
        }

        public static @Nullable IAnimation getAnimation(AbstractClientPlayer player) {
            ModifierLayer<IAnimation> animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player)
                    .get(Fantazia.res("animation"));
            if (animationLayer != null) {
                return animationLayer.getAnimation();
            } else {
                return null;
            }
    }
        public static List<LivingEntity> getTargets(@NotNull LivingEntity caster, float range, float maxDist, boolean seeThruWalls) {
            Vector3 head = Vector3.fromEntityCenter(caster);
            List<LivingEntity> entities = new ArrayList<>();

            for (int distance = 1; distance < maxDist; ++distance) {
                head = head.add(new Vector3(caster.getLookAngle()).multiply(distance)).add(0.0, 0.5, 0.0);
                List<LivingEntity> list = caster.level().getEntitiesOfClass(LivingEntity.class, new AABB(head.x - range, head.y - range, head.z - range, head.x + range, head.y + range, head.z + range));
                list.removeIf(entity -> (entity == caster || (!caster.hasLineOfSight(entity) && !(seeThruWalls && Minecraft.getInstance().shouldEntityAppearGlowing(entity)))));
                entities.addAll(list);
            }

            return entities;
        }
        @Nullable
        public static LivingEntity getClosestEntity(List<LivingEntity> entities, LivingEntity player) {
            if (entities.isEmpty()) { return null; }
            if (entities.size() == 1) { return entities.get(0); }
            Map<Double, LivingEntity> livingEntityMap = new HashMap<>();
            List<Double> distances = new ArrayList<>();
            for (LivingEntity entity : entities) {
                double distance = entity.distanceTo(player);
                livingEntityMap.put(distance, entity);
                distances.add(distance);
            }
            distances.sort(Comparator.comparing(Double::doubleValue));
            return livingEntityMap.get(distances.get(0));
        }
        public enum ParticleMovement {
            REGULAR, CHASE, FALL, ASCEND, CHASE_AND_FALL, CHASE_OPPOSITE, CHASE_AND_FALL_OPPOSITE, FROM_CENTER, TO_CENTER
        }
        public static void randomParticleOnModel(Entity entity, @Nullable SimpleParticleType particle, ParticleMovement type) {
            if (particle == null) { return; }
            // getting entity's height and width
            float radius = entity.getBbWidth() * (float) 0.7;
            float height = entity.getBbHeight();

            double dx = entity.getDeltaMovement().x();
            double dy = entity.getDeltaMovement().y();
            double dz = entity.getDeltaMovement().z();

            // here im using circular function for X and Z (X**2 + Z**2 = R**2) coordinates to make a horizontal circle
            // Y variants are just a vertical line
            double y = Fantazia.RANDOM.nextDouble(0, height * 0.8);
            double x = Fantazia.RANDOM.nextDouble(-radius, radius);
            double z = java.lang.Math.sqrt(radius * radius - x * x);

            // here game randomly decides to make Z coordinate negative
            boolean negativeZ = Fantazia.RANDOM.nextBoolean();
            z = negativeZ ? z * (-1) : z;
            if (Minecraft.getInstance().level != null) {
                switch (type) {
                    case REGULAR -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            0, 0, 0);
                    case CHASE -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            dx * 1.5, dy * 0.2 + 0.1, dz * 1.5);
                    case FALL -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            0, -0.15, 0);
                    case ASCEND -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            0,0.15,0);
                    case CHASE_AND_FALL -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            dx * 1.5, 00.15, dz * 1.5);
                    case CHASE_OPPOSITE -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            -dx * 1.5, -(dy * 0.2 + 0.1), -dz * 1.5);
                    case CHASE_AND_FALL_OPPOSITE -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            -dx * 1.5, -0.15, -dz * 1.5);
                    case FROM_CENTER -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            x * 1.5, y * 1.5, z * 1.5);
                    case TO_CENTER -> Minecraft.getInstance().level.addParticle(particle, true,
                            entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                            -x * 1.5, -y * 1.5, -z * 1.5);
                }
            }
        }
        public static boolean tryToGetStuck(LivingEntity entity, ItemStack stack) {
            if (hatchetStuck.containsKey(entity)) return false;
            hatchetStuck.put(entity, stack);
            return true;
        }
        public static Vec3 headOfEntity(LivingEntity livingEntity) {
            AABB aabb = livingEntity.getBoundingBox();
            double minX = aabb.minX;
            double minY = aabb.minY;
            double minZ = aabb.minZ;
            double xHalf = aabb.getXsize() / 2f;
            double dist = livingEntity.getEyeHeight(livingEntity.getPose());
            double zHalf = aabb.getZsize() / 2f;
            return new Vec3(minX + xHalf, minY + dist, minZ + zHalf);
        }
        public static boolean facesAttack(LivingEntity blocker, DamageSource source) {
            Vec3 vec32 = source.getSourcePosition();
            if (vec32 != null) {
                Vec3 vec3 = blocker.getViewVector(1.0F);
                Vec3 vec31 = vec32.vectorTo(blocker.position()).normalize();
                vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
                return vec31.dot(vec3) < 0.0D;
            } else return false;
        }
        public static float calculateBleedingDMG(LivingEntity entity, Vec3 vec3) {
            float movement = (float) vec3.horizontalDistance() / 50f;
            if (entity instanceof Player player) {
                AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                if (abilityManager != null) {
                    Dash dash = abilityManager.takeAbility(Dash.class);
                    if (dash != null && dash.isDashing() && dash.getLevel() <= 1) {
                        return 7.5f;
                    }
                }
                if (player.isSprinting()) {
                    return 1.5f * movement;
                } else if (player.isCrouching()) {
                    return 0.0625f * movement;
                }
            }
            return movement;
        }
        public static boolean hasBarrier(LivingEntity livingEntity) {
            EffectManager effectManager = EffectGetter.getUnwrap(livingEntity);
            if (effectManager == null) return false;

            BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
            if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

            LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
            if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

            AbsoluteBarrierEffect absoluteBarrierEffect = effectManager.takeEffect(AbsoluteBarrierEffect.class);
            if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;

            return false;
        }
        public static boolean hurtRedColor(LivingEntity livingEntity) {
            if (hasBarrier(livingEntity)) return false;
            AtomicReference<Boolean> flag = new AtomicReference<>(true);
            if (livingEntity.getLastDamageSource() != null) {
                notGlowRed.forEach(source -> {
                    if (livingEntity.getLastDamageSource().is(source)) {
                        flag.set(false);
                    }
                });
            }
            return flag.get();
        }
    }
    public static class ModdedEntities {
        public enum Direction {
            ONLY$X, ONLY$Y, ONLY$Z, X$Y, X$Z, Y$Z, XYZ;
        }
        public static void hatchetRicochet(HatchetEntity entity, Direction direction, float spdMultip) {
            Vec3 vec3 = entity.getDeltaMovement();
            Vec3 newV3 = switch (direction) {
                case ONLY$X -> vec3.subtract(vec3.x() * 2,0,0).scale(spdMultip);
                case ONLY$Y -> vec3.subtract(0,vec3.y() * 2,0).scale(spdMultip);
                case ONLY$Z -> vec3.subtract(0,0,vec3.z() * 2).scale(spdMultip);
                case X$Y -> vec3.subtract(vec3.x() * 2,vec3.y() * 2,0).scale(spdMultip);
                case X$Z -> vec3.subtract(vec3.x() * 2,0,vec3.z() * 2).scale(spdMultip);
                case Y$Z -> vec3.subtract(0,vec3.y() * 2,vec3.z() * 2).scale(spdMultip);
                case XYZ -> vec3.subtract(vec3.x() * 2, vec3.y() * 2, vec3.z() * 2).scale(spdMultip);
            };
            entity.setDeltaMovement(newV3);
        }
        public static void hatchetRicochet(HatchetEntity entity, Direction direction) {
            hatchetRicochet(entity, direction, 1f);
        }
    }
    public static class Math {
        public static boolean withinClamp(int min, int max, int num) {
            return num >= min && num <= max;
        }
        public static boolean withinClamp(float min, float max, float num) {
            return num >= min && num <= max;
        }
        public static boolean withinClamp(double min, double max, double num) {
            return num >= min && num <= max;
        }

        // solves quadratic equation and returns list of results from lowest to highest;
        public static List<Double> solvePol(double a, double b, double c) {
            List<Double> solves = new ArrayList<>();
            double x;
            if (a == 0) {
                solves.add(-c / b);
            } else if (b == 0) {
                if ((-c / a) >= 0) {
                    solves.add(java.lang.Math.sqrt(-c / a));
                    solves.add(-java.lang.Math.sqrt(-c / a));
                }
            } else if (c == 0) {
                solves.add(0D);
                solves.add(-b / a);
            } else {
                double D = b * b - 4 * a * c;
                if (D == 0) {
                    solves.add(-b / (2 * a));
                } else if (D > 0) {
                    solves.add((-b + java.lang.Math.sqrt(D)) / (2 * a));
                    solves.add((-b - java.lang.Math.sqrt(D)) / (2 * a));
                }
            }
            Collections.sort(solves);
            return solves;
        }
        public static class RandomGeometricFigureSides {
            private static Random random = new Random();
            public static Vec3 cube(double x0, double y0, double z0, double halfSife) {
                double x = x0 + random.nextDouble(-halfSife, halfSife);
                double y = y0 + random.nextDouble(-halfSife, halfSife);
                double z = z0 + random.nextDouble(-halfSife, halfSife);
                return switch (random.nextInt(0, 6)) {
                    default -> new Vec3(x + halfSife, y + halfSife, z + halfSife);
                    case 0 -> new Vec3(x, y0 + halfSife, z);
                    case 1 -> new Vec3(x, y0 - halfSife, z);
                    case 2 -> new Vec3(x0 + halfSife, y, z);
                    case 3 -> new Vec3(x0 - halfSife, y, z);
                    case 4 -> new Vec3(x, y, z0 + halfSife);
                    case 5 -> new Vec3(x, y, z0 - halfSife);
                };
            }
            public static Vec3 cube(Vec3 center, float halfSife) {
                return cube(center.x(), center.y(), center.z(), halfSife);
            }

        }
        public static Vec3 findCenter(Vec3 point1, Vec3 point2) {
            Vec3 vec3 = point1.subtract(point2);
            Vec3 vec31 = vec3.scale(0.5f);
            return point2.add(vec31);
        }
    }
    public static class LootTables {
        public static LootPool constructLootPool(String poolName, float minRolls, float maxRolls, @Nullable LootPoolEntryContainer.Builder<?>... entries) {
            LootPool.Builder poolBuilder = LootPool.lootPool();
            poolBuilder.name(poolName);
            poolBuilder.setRolls(UniformGenerator.between(minRolls, maxRolls));

            for (LootPoolEntryContainer.Builder<?> entry : entries) {
                if (entry != null) {
                    poolBuilder.add(entry);
                }
            }
            return poolBuilder.build();
        }
        public static LootPoolSingletonContainer.Builder<?> createOptionalLoot(Item item, int weight, float minCount, float maxCount) {
            return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount)));
        }
        public static LootPoolSingletonContainer.Builder<?> createOptionalLoot(Item item, int weight) {
            return LootItem.lootTableItem(item).setWeight(weight);
        }
        public static List<ResourceLocation> getTempleLootTable() {
            List<ResourceLocation> lootChestList = new ArrayList<>();
            lootChestList.add(BuiltInLootTables.DESERT_PYRAMID);
            lootChestList.add(BuiltInLootTables.JUNGLE_TEMPLE);

            return lootChestList;
        }
        public static List<ResourceLocation> getAncientCityLootTable() {
            List<ResourceLocation> lootChestList = new ArrayList<>();
            lootChestList.add(BuiltInLootTables.ANCIENT_CITY);
            lootChestList.add(BuiltInLootTables.ANCIENT_CITY_ICE_BOX);

            return lootChestList;
        }
        public static List<ResourceLocation> getNetherLootTable() {
            List<ResourceLocation> lootChestList = new ArrayList<>();
            lootChestList.add(BuiltInLootTables.BASTION_BRIDGE);
            lootChestList.add(BuiltInLootTables.BASTION_TREASURE);
            lootChestList.add(BuiltInLootTables.NETHER_BRIDGE);
            lootChestList.add(BuiltInLootTables.BASTION_HOGLIN_STABLE);
            lootChestList.add(BuiltInLootTables.BASTION_OTHER);

            return lootChestList;
        }
    }
}
