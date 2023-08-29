package net.arkadiyhimself.combatimprovement.HandlersAndHelpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.types.Func;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.NewEvents;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.SpellCasters;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class UsefulMethods {
    static Random random = new Random();
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
            if (objs != null) {

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
            }
            if (strFormat == null) {
                list.add(Component.translatable(str, stringValues));
            } else {
                list.add(Component.translatable(str, stringValues).withStyle(strFormat));
            }
        }
    }
    public static class Abilities {
        public static void dropExperience(LivingEntity entity, float multiplier) {
            if (entity.level instanceof ServerLevel) {
                int reward = (int) (entity.getExperienceReward() * multiplier);
                ExperienceOrb.award((ServerLevel)entity.level, entity.position(), reward);
            }
        }
        public static void addEffectWithoutParticles(LivingEntity entity, MobEffect effect, int duration, int level) {
            entity.addEffect(new MobEffectInstance(effect, duration, level, true, false, true));
        }
        public static void addEffectWithoutParticles(LivingEntity entity, MobEffect effect, int duration) {
            addEffectWithoutParticles(entity, effect, duration, 0);
        }
        public static boolean canNotDoActions(Player player) {
            if (player == null) { return true; }
            if (AttachDash.getUnwrap(player) != null && AttachBlocking.getUnwrap(player) != null && StunEffect.getUnwrap(player) != null) {
                return AttachDash.getUnwrap(player).isDashing() || AttachBlocking.getUnwrap(player).isInAnim() || StunEffect.getUnwrap(player).isStunned();
            }
            return false;
        }
        public static boolean blocksDamage(LivingEntity entity) {
            if (entity instanceof Player player) {
                if (AttachDash.getUnwrap(player).isDashing() && AttachDash.getUnwrap(player).dashLevel > 1) { return true; }
            }
            return BarrierEffect.getUnwrap(entity).hasBarrier() || LayeredBarrierEffect.getUnwrap(entity).hasBarrier();
        }
        public static boolean hasCurio(final LivingEntity entity, final Item curio) {
            final Optional<ImmutableTriple<String, Integer, ItemStack>> data = CuriosApi.getCuriosHelper().findEquippedCurio(curio, entity);
            return data.isPresent();
        }
        public static List<Double> calculateDashHorizontalVelocity(LivingEntity entity, double velocity) {
            return calculateDashHorizontalVelocity(entity.getLookAngle().x(), entity.getLookAngle().z(), velocity);
        }
        public static List<Double> calculateDashHorizontalVelocity(double xRot, double zRot, double velocity) {
            List<Double> multipliers = new ArrayList<>() {};
            if (zRot == 0) {
                multipliers.add(xRot * velocity * 0.5);
                multipliers.add(0d);
            } else {
                double ratio = xRot / zRot;
                boolean xNeg = xRot < 0;
                boolean zNeg = zRot < 0;
                double dZ = (velocity) / Math.sqrt(1 + ratio * ratio);
                double dX = Math.abs(dZ * ratio);
                multipliers.add(xNeg ? -dX : dX);
                multipliers.add(zNeg ? -dZ : dZ);
            }
            return multipliers;
        }
        public static List<Double> calculateDashVelocity(LivingEntity entity, double velocity) {
            return calculateDashVelocity(entity.getLookAngle(), velocity);
        }
        public static List<Double> calculateDashVelocity(Vec3 angle, double velocity) {
            return calculateDashVelocity(angle.x(), angle.y(), angle.z(), velocity);
        }
        public static List<Double> calculateDashVelocity(double xRot, double yRot, double zRot, double velocity) {
            List<Double> multipliers = new ArrayList<>() {};
            multipliers.add(xRot * velocity * 0.5);
            multipliers.add(yRot * velocity * 0.5);
            multipliers.add(zRot * velocity * 0.5);
            return multipliers;
        }
        public static int thirdLevelDashDurationHorizontal(ServerPlayer player, double dx, double dz, int initDuration) {
            double x0 = player.getX();
            double z0 = player.getZ();
            float multiplier = 2.4f;

            int m = 0;
            for (int i = initDuration; i > 0; i--) {
                Vec3 finalPos = new Vec3(x0 + dx / multiplier * i, player.getY() + 1,z0 + dz / multiplier * i);
                if (player.level.getBlockState(new BlockPos(finalPos)).getBlock() instanceof AirBlock) {
                    m = i;
                    break;
                }
            }
            return m;
        }
        public static void animatePlayer(Player player, @Nullable String name) {
            ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) player)
                    .get(new ResourceLocation(CombatImprovement.MODID, "animation"));
            if (animation != null) {
                if (name != null) {
                    @Nullable KeyframeAnimation keyframeAnimation = PlayerAnimationRegistry.getAnimation
                            (new ResourceLocation(CombatImprovement.MODID, name));
                    if (keyframeAnimation != null) {
                        animation.setAnimation(new KeyframeAnimationPlayer(keyframeAnimation));
                    }
                } else {
                    animation.setAnimation(null);
                }
            }
        }
        public static void doubleJump(ServerPlayer serverPlayer) {
            boolean doJump = NewEvents.onDoubleJump(serverPlayer);
            if (doJump) {
                serverPlayer.level.playSound(null, serverPlayer.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS);
                Vec3 vec3 = serverPlayer.getDeltaMovement();
                serverPlayer.setDeltaMovement(vec3.x, 0.64 + serverPlayer.getJumpBoostPower(), vec3.z);
                serverPlayer.hurtMarked = true;
            }
        }
        public static List<LivingEntity> getTargets(@NotNull Player player, float range, int maxDist) {
            Vector3 head = Vector3.fromEntityCenter(player);
            List<LivingEntity> entities = new ArrayList<>();

            for (int distance = 1; distance < maxDist; ++distance) {
                head = head.add(new Vector3(player.getLookAngle()).multiply(distance)).add(0.0, 0.5, 0.0);
                List<LivingEntity> list = player.level.getEntitiesOfClass(LivingEntity.class, new AABB(head.x - range, head.y - range, head.z - range, head.x + range, head.y + range, head.z + range));
                list.removeIf(entity -> entity == player || !player.hasLineOfSight(entity));
                entities.addAll(list);
            }

            return entities;
        }
        @Nullable
        public static LivingEntity getClosestEntity(List<LivingEntity> entities, Player player) {
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
            REGULAR, CHASE, FALL, CHASE_AND_FALL, CHASE_OPPOSITE, CHASE_AND_FALL_OPPOSITE
        }
        public static void createRandomParticleOnHumanoid(LivingEntity entity, @Nullable SimpleParticleType particle, ParticleMovement type) {
            if (particle == null) { return; }
            // getting entity's height and width
            float radius = entity.getBbWidth() * (float) 0.7;
            float height = entity.getBbHeight();

            double dx = entity.getDeltaMovement().x();
            double dy = entity.getDeltaMovement().y();
            double dz = entity.getDeltaMovement().z();

            // here im using circular function for X and Z (X**2 + Z**2 = R**2) coordinates to make a horizontal circle
            // Y variants are just a vertical line
            double y = random.nextDouble(0, height * 0.8);
            double x = random.nextDouble(-radius, radius);
            double z = Math.sqrt(radius * radius - x * x);

            // here game randomly decides to make Z coordinate negative
            boolean negativeZ = random.nextBoolean();
            z = negativeZ ? z * (-1) : z;
            if (Minecraft.getInstance().level != null) {
                switch (type) {
                    case REGULAR -> {
                        Minecraft.getInstance().level.addParticle(particle, true,
                                entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                                0, 0, 0);
                    }
                    case CHASE -> {
                        Minecraft.getInstance().level.addParticle(particle, true,
                                entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                                dx * 1.5, dy * 0.2 + 0.1, dz * 1.5);
                    }
                    case FALL -> {
                        Minecraft.getInstance().level.addParticle(particle, true,
                                entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                                0, -0.15, 0);
                    }
                    case CHASE_AND_FALL -> {
                        Minecraft.getInstance().level.addParticle(particle, true,
                                entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                                dx * 1.5, 00.15, dz * 1.5);
                    }
                    case CHASE_OPPOSITE -> {
                        Minecraft.getInstance().level.addParticle(particle, true,
                                entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                                -dx * 1.5, -(dy * 0.2 + 0.1), -dz * 1.5);
                    }
                    case CHASE_AND_FALL_OPPOSITE -> {
                        Minecraft.getInstance().level.addParticle(particle, true,
                                entity.getX() + x, entity.getY() + y, entity.getZ() + z,
                                -dx * 1.5, -0.15, -dz * 1.5);
                    }
                }
            }
        }
        public static boolean blockedAttack(Player blocker, DamageSource source) {
            Vec3 vec32 = source.getSourcePosition();
            if (vec32 != null) {
                Vec3 vec3 = blocker.getViewVector(1.0F);
                Vec3 vec31 = vec32.vectorTo(blocker.position()).normalize();
                vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
                if (vec31.dot(vec3) < 0.0D) {
                    return true;
                } else { return false; }
            } else { return false; }
        }
    }
    public static class Mathematics {

        // solves quadratic equation and returns list of results from lowest to highest;
        public static List<Double> solvePol(double a, double b, double c) {
            List<Double> solves = new ArrayList<>();
            double x;
            if (a == 0) {
                solves.add(-c / b);
            } else if (b == 0) {
                if ((-c / a) >= 0) {
                    solves.add(Math.sqrt(-c / a));
                    solves.add(-Math.sqrt(-c / a));
                }
            } else if (c == 0) {
                solves.add(0D);
                solves.add(-b / a);
            } else {
                double D = b * b - 4 * a * c;
                if (D == 0) {
                    solves.add(-b / (2 * a));
                } else if (D > 0) {
                    solves.add((-b + Math.sqrt(D)) / (2 * a));
                    solves.add((-b - Math.sqrt(D)) / (2 * a));
                }
            }
            Collections.sort(solves);
            return solves;
        }
        public static class randomGeometricFigureSides {
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
    }
    public static class RegisterStuff {
        public static void registerItem() {

        }
        public static void registerCurio(final String identifier, final int slots, final boolean isHidden, @Nullable final ResourceLocation icon) {
            final SlotTypeMessage.Builder message = new SlotTypeMessage.Builder(identifier);

            message.size(slots);

            if (isHidden) {
                message.hide();
            }

            if (icon != null) {
                message.icon(icon);
            }

            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> message.build());
        }
        public static void registerCurioItem() {

        }
        public static void registerMobEffect() {

        }
        public static void registerSoundEvent() {

        }
        public static void registerParticle() {
        }
    }
}
