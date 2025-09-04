package net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.enchantment.effects.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class FTZEnchantmentEffectComponentTypes {

    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Fantazia.MODID);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Codec<T> codec) {
        return REGISTER.registerComponentType(name, tBuilder -> tBuilder.persistent(codec));
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<ConvertLootToExp>>>> EQUIPMENT_CONVERT;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<CriticalStrikeModify>>>> CRITICAL_DAMAGE_MODIFY;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<ParryModify>>>> PARRY_MODIFY;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Amplification>>> AMPLIFICATION_LEVEL;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<RandomChanceOccurrence>>>> DECAPITATION;

    static {
        EQUIPMENT_CONVERT = register("equipment_drops",
                TargetedConditionalEffect.equipmentDropsCodec(ConvertLootToExp.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());

        CRITICAL_DAMAGE_MODIFY = register("critical_damage_modify",
                TargetedConditionalEffect.equipmentDropsCodec(CriticalStrikeModify.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());

        PARRY_MODIFY = register("parry_modify",
                TargetedConditionalEffect.equipmentDropsCodec(ParryModify.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());

        AMPLIFICATION_LEVEL = register("amplification_level",
                Amplification.CODEC.listOf());

        DECAPITATION = register("decapitation",
                TargetedConditionalEffect.equipmentDropsCodec(RandomChanceOccurrence.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf());
    }
}
