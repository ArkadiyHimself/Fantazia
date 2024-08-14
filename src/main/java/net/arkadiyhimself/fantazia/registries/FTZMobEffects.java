package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.mobeffects.SimpleMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FTZMobEffects extends FTZRegistry<MobEffect> {
    @SuppressWarnings("unused")
    private static final FTZMobEffects INSTANCE = new FTZMobEffects();
    @ObjectHolder(value = Fantazia.MODID + ":haemorrhage", registryName = "mob_effect")
    public static final SimpleMobEffect HAEMORRHAGE = null;
    @ObjectHolder(value = Fantazia.MODID + ":fury", registryName = "mob_effect")
    public static final SimpleMobEffect FURY = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":stun", registryName = "mob_effect")
    public static final SimpleMobEffect STUN = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":barrier", registryName = "mob_effect")
    public static final SimpleMobEffect BARRIER = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":layered_barrier", registryName = "mob_effect")
    public static final SimpleMobEffect LAYERED_BARRIER = null;
    @ObjectHolder(value = Fantazia.MODID + ":absolute_barrier", registryName = "mob_effect")
    public static final SimpleMobEffect ABSOLUTE_BARRIER = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":deafened", registryName = "mob_effect")
    public static final SimpleMobEffect DEAFENED = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":frozen", registryName = "mob_effect")
    public static final SimpleMobEffect FROZEN = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":might", registryName = "mob_effect")
    public static final SimpleMobEffect MIGHT = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":doomed", registryName = "mob_effect")
    public static final SimpleMobEffect DOOMED = null;
    @ObjectHolder(value = Fantazia.MODID + ":disarm", registryName = "mob_effect")
    public static final SimpleMobEffect DISARM = null;
    @ObjectHolder(value = Fantazia.MODID + ":reflect", registryName = "mob_effect")
    public static final SimpleMobEffect REFLECT = null;
    @ObjectHolder(value = Fantazia.MODID + ":deflect", registryName = "mob_effect")
    public static final SimpleMobEffect DEFLECT = null;
    @ObjectHolder(value = Fantazia.MODID + ":microstun", registryName = "mob_effect")
    public static final SimpleMobEffect MICROSTUN = null;
    @ObjectHolder(value = Fantazia.MODID + ":corrosion", registryName = "mob_effect")
    public static final SimpleMobEffect CORROSION = null;
    @ObjectHolder(value = Fantazia.MODID + ":mana_boost", registryName = "mob_effect")
    public static final SimpleMobEffect MANA_BOOST = null;

    @ObjectHolder(value = Fantazia.MODID + ":stamina_boost", registryName = "mob_effect")
    public static final SimpleMobEffect STAMINA_BOOST = null;
    @SuppressWarnings("ConstantConditions")
    private FTZMobEffects() {
        super(ForgeRegistries.MOB_EFFECTS);

        this.register("haemorrhage", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 6553857, true));
        this.register("fury", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 16057348, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, "5b9aec64-4a33-11ee-be56-0242ac120002", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.register("stun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, "103503fe-4a33-11ee-be56-0242ac120002", -10, AttributeModifier.Operation.ADDITION));
        this.register("barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "21fe121a-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        this.register("layered_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 126,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "2c3db8d4-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        this.register("absolute_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 7995643,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "444492cc-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        this.register("deafened", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 4693243,true));
        this.register("frozen", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 8780799, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, "4b3d404c-4a33-11ee-be56-0242ac120002", -0.25f, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, "500fbb5e-4a33-11ee-be56-0242ac120002", -0.6f, AttributeModifier.Operation.ADDITION));
        this.register("might", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16767061,true).addAttributeModifier(Attributes.ATTACK_DAMAGE, "5502680a-4a33-11ee-be56-0242ac120002", 1, AttributeModifier.Operation.ADDITION));
        this.register("doomed", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0, true));
        this.register("disarm", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16447222,true));
        this.register("reflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true));
        this.register("deflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true));
        this.register("microstun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691,false));
        this.register("corrosion", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16057348,true).addAttributeModifier(Attributes.ARMOR, "df08f537-e143-4f64-90e8-4a46c505de44", -1f, AttributeModifier.Operation.ADDITION));
        this.register("mana_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true).addAttributeModifier(FTZAttributes.MAX_MANA, "5baca776-6b3a-4b35-b8b4-f5cbbf4eef8a", 4f, AttributeModifier.Operation.ADDITION));
        this.register("stamina_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true).addAttributeModifier(FTZAttributes.MAX_STAMINA, "a68bc117-5fd8-4864-9758-56233c8f26c9", 4f, AttributeModifier.Operation.ADDITION));
    }
    @Override
    protected RegistryObject<MobEffect> register(String name, Supplier<? extends MobEffect> supplier) {
        return super.register(name, supplier);
    }
    public static class Application {
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
