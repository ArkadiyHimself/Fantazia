package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.simpleobjects.SimpleMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FTZMobEffects {
    private FTZMobEffects() {}
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, Fantazia.MODID);
    public static final DeferredHolder<MobEffect, SimpleMobEffect> HAEMORRHAGE = REGISTER.register("haemorrhage", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 6553857, true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> FURY = REGISTER.register("fury", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 16057348, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.res("effect.fury"), 0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> STUN = REGISTER.register("stun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.res("effect.stun"), -10, AttributeModifier.Operation.ADD_VALUE));// finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> BARRIER = REGISTER.register("barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.res("effect.barrier"), 0.5, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> LAYERED_BARRIER = REGISTER.register("layered_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 126,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.res("effect.layered_barrier"), 0.5, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> ABSOLUTE_BARRIER = REGISTER.register("absolute_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 7995643,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.res("effect.absolute_barrier"), 0.5, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DEAFENED = REGISTER.register("deafened", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 4693243,true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> FROZEN = REGISTER.register("frozen", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 8780799, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.res("effect.frozen"), -0.25f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, Fantazia.res("effect.frozen"), -0.6f, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> MIGHT = REGISTER.register("might", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16767061,true).addAttributeModifier(Attributes.ATTACK_DAMAGE, Fantazia.res("effect.might"), 1, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DOOMED = REGISTER.register("doomed", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0, true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DISARM = REGISTER.register("disarm", () -> new  SimpleMobEffect(MobEffectCategory.HARMFUL, 16447222,true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> REFLECT = REGISTER.register("reflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DEFLECT = REGISTER.register("deflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> MICROSTUN = REGISTER.register("microstun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691,false)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> CORROSION = REGISTER.register("corrosion", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16057348,true).addAttributeModifier(Attributes.ARMOR, Fantazia.res("effect.corrosion"), -1f, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> MANA_BOOST = REGISTER.register("mana_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true).addAttributeModifier(FTZAttributes.MAX_MANA, Fantazia.res("effect.mana_boost"), 4f, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> STAMINA_BOOST = REGISTER.register("stamina_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true).addAttributeModifier(FTZAttributes.MAX_STAMINA, Fantazia.res("effect.stamina_boost"), 4f, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> CURSED_MARK = REGISTER.register("cursed_mark", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0, true)); // finished and implemented

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    public static class Application {
        private Application() {}
        public static boolean isApplicable(EntityType<?> entityType, Holder<MobEffect> mobEffect) {
            return isAffected(entityType, mobEffect) && !isImmune(entityType, mobEffect);
        }
        public static boolean isAffected(EntityType<?> entityType, Holder<MobEffect> mobEffect) {
            if (!hasWhiteList(mobEffect)) return true;
            return entityType.is(getWhiteList(mobEffect));
        }
        public static boolean isImmune(EntityType<?> entityType, Holder<MobEffect> mobEffect) {
            if (!hasBlackList(mobEffect)) return false;
            return entityType.is(getBlackList(mobEffect));
        }
        @NotNull
        private static TagKey<EntityType<?>> getWhiteList(Holder<MobEffect> mobEffect) {
            ResourceLocation resLoc = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect.value());
            if (resLoc == null) return create("empty");
            return create("affected/" + resLoc.getNamespace() + "/" + resLoc.getPath());
        }
        @NotNull
        private static TagKey<EntityType<?>> getBlackList(Holder<MobEffect> mobEffect) {
            ResourceLocation resLoc = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect.value());
            if (resLoc == null) return create("empty");
            return create("immune/" + resLoc.getNamespace() + "/" + resLoc.getPath());
        }
        private static boolean hasWhiteList(Holder<MobEffect> mobEffect) {
            Optional<HolderSet.Named<EntityType<?>>> iTagManager = BuiltInRegistries.ENTITY_TYPE.getTag(getWhiteList(mobEffect));
            return iTagManager.isPresent() && !iTagManager.get().stream().toList().isEmpty();
        }
        private static boolean hasBlackList(Holder<MobEffect> mobEffect) {
            Optional<HolderSet.Named<EntityType<?>>> iTagManager = BuiltInRegistries.ENTITY_TYPE.getTag(getBlackList(mobEffect));
            return iTagManager.isPresent() && !iTagManager.get().stream().toList().isEmpty();
        }
        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, Fantazia.res(pName));
        }
    }
}
