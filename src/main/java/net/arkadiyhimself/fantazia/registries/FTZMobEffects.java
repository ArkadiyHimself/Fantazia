package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.simpleobjects.SimpleMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

public class FTZMobEffects {
    private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Fantazia.MODID);
    public static final RegistryObject<MobEffect> HAEMORRHAGE = REGISTER.register("haemorrhage", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 6553857, true)); // finished and implemented
    public static final RegistryObject<MobEffect> FURY = REGISTER.register("fury", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 16057348, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, "5b9aec64-4a33-11ee-be56-0242ac120002", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    // finished and implemented
    public static final RegistryObject<MobEffect> STUN = REGISTER.register("stun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, "103503fe-4a33-11ee-be56-0242ac120002", -10, AttributeModifier.Operation.ADDITION));
    // finished and implemented
    public static final RegistryObject<MobEffect> BARRIER = REGISTER.register("barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "21fe121a-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION)); // finished and implemented
    public static final RegistryObject<MobEffect> LAYERED_BARRIER = REGISTER.register("layered_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 126,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "2c3db8d4-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> ABSOLUTE_BARRIER = REGISTER.register("absolute_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 7995643,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "444492cc-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION)); // finished and implemented
    public static final RegistryObject<MobEffect> DEAFENED = REGISTER.register("deafened", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 4693243,true)); // finished and implemented
    public static final RegistryObject<MobEffect> FROZEN = REGISTER.register("frozen", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 8780799, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, "4b3d404c-4a33-11ee-be56-0242ac120002", -0.25f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, "500fbb5e-4a33-11ee-be56-0242ac120002", -0.6f, AttributeModifier.Operation.ADDITION)); // finished and implemented
    public static final RegistryObject<MobEffect> MIGHT = REGISTER.register("might", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16767061,true).addAttributeModifier(Attributes.ATTACK_DAMAGE, "5502680a-4a33-11ee-be56-0242ac120002", 1, AttributeModifier.Operation.ADDITION)); // finished and implemented
    public static final RegistryObject<MobEffect> DOOMED = REGISTER.register("doomed", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0, true));
    public static final RegistryObject<MobEffect> DISARM = REGISTER.register("disarm", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16447222,true)); // finished and implemented
    public static final RegistryObject<MobEffect> REFLECT = REGISTER.register("reflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)); // finished and implemented
    public static final RegistryObject<MobEffect> DEFLECT = REGISTER.register("deflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)); // finished and implemented
    public static final RegistryObject<MobEffect> MICROSTUN = REGISTER.register("microstun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691,false)); // finished and implemented
    public static final RegistryObject<MobEffect> CORROSION = REGISTER.register("corrosion", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16057348,true).addAttributeModifier(Attributes.ARMOR, "df08f537-e143-4f64-90e8-4a46c505de44", -1f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> MANA_BOOST = REGISTER.register("mana_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true).addAttributeModifier(FTZAttributes.MAX_MANA.get(), "5baca776-6b3a-4b35-b8b4-f5cbbf4eef8a", 4f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> STAMINA_BOOST = REGISTER.register("stamina_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true).addAttributeModifier(FTZAttributes.MAX_STAMINA.get(), "a68bc117-5fd8-4864-9758-56233c8f26c9", 4f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> CURSED_MARK = REGISTER.register("cursed_mark", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0, true));
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    public static class Application {
        public static boolean isApplicable(EntityType<?> entityType, MobEffect mobEffect) {
            return isAffected(entityType, mobEffect) && !isImmune(entityType, mobEffect);
        }
        public static boolean isAffected(EntityType<?> entityType, MobEffect mobEffect) {
            if (!hasWhiteList(mobEffect)) return true;
            return entityType.is(getWhiteList(mobEffect));
        }
        public static boolean isImmune(EntityType<?> entityType, MobEffect mobEffect) {
            if (!hasBlackList(mobEffect)) return false;
            return entityType.is(getBlackList(mobEffect));
        }
        @NotNull
        private static TagKey<EntityType<?>> getWhiteList(MobEffect mobEffect) {
            ResourceLocation resLoc = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
            if (resLoc == null) return create("empty");
            return create("affected/" + resLoc.getNamespace() + "/" + resLoc.getPath());
        }
        @NotNull
        private static TagKey<EntityType<?>> getBlackList(MobEffect mobEffect) {
            ResourceLocation resLoc = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
            if (resLoc == null) return create("empty");
            return create("immune/" + resLoc.getNamespace() + "/" + resLoc.getPath());
        }
        private static boolean hasWhiteList(MobEffect mobEffect) {
            ITagManager<EntityType<?>> iTagManager = ForgeRegistries.ENTITY_TYPES.tags();
            return iTagManager != null && !iTagManager.getTag(getWhiteList(mobEffect)).isEmpty();
        }
        private static boolean hasBlackList(MobEffect mobEffect) {
            ITagManager<EntityType<?>> iTagManager = ForgeRegistries.ENTITY_TYPES.tags();
            return iTagManager != null && !iTagManager.getTag(getBlackList(mobEffect)).isEmpty();
        }
        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, Fantazia.res(pName));
        }
    }
}
