package net.arkadiyhimself.fantazia.registry;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTE =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Fantazia.MODID);
    public static void register(IEventBus eventBus) {
        ATTRIBUTE.register(eventBus);
    }

    public static final RegistryObject<Attribute> MAX_MANA;  // implemented
    public static final RegistryObject<Attribute> MAX_STAMINA;  // implemented
    public static final RegistryObject<Attribute> MAX_STUN_POINTS;  // implemented
    public static final RegistryObject<Attribute> MANA_REGEN_MULTIPLIER; // implemented
    public static final RegistryObject<Attribute> STAMINA_REGEN_MULTIPLIER; // implemented

    public static final RegistryObject<Attribute> CAST_RANGE_BASE_MULTIPLIER; // implemented
    public static final RegistryObject<Attribute> CAST_RANGE_ADDITION; // implemented
    public static final RegistryObject<Attribute> CAST_RANGE_TOTAL_MULTIPLIER; // implemented

    public static final RegistryObject<Attribute> RECHARGE_MULTIPLIER;

    public static final RegistryObject<Attribute> LIFESTEAL;
    static {
        MAX_MANA = ATTRIBUTE.register("max_mana",
                () -> new RangedAttribute("attribute.fantazia.max_mana",
                        20f,0f,4096f));
        MAX_STAMINA = ATTRIBUTE.register("max_stamina",
                () -> new RangedAttribute("attribute.fantazia.max_stamina",
                        20f,0f,4096f));
        MAX_STUN_POINTS = ATTRIBUTE.register("max_stun_points",
                () -> new RangedAttribute("attribute.fantazia.max_stun_points",
                        200f,1f,4096f));

        MANA_REGEN_MULTIPLIER = ATTRIBUTE.register("mana_regen_multiplier",
                        () -> new RangedAttribute("attribute.fantazia.mana_regen_multiplier",
                                1f, 0f, 256f).setSyncable(true));
        STAMINA_REGEN_MULTIPLIER = ATTRIBUTE.register("stamina_regen_multiplier",
                        () -> new RangedAttribute("attribute.fantazia.stamina_regen_multiplier",
                                1f, 0f, 256f).setSyncable(true));

        CAST_RANGE_BASE_MULTIPLIER = ATTRIBUTE.register("cast_range_base_multiplier",
                () -> new RangedAttribute("attribute.fantazia.cast_range_base_multiplier",
                        1f, 0f, 256f));
        CAST_RANGE_ADDITION = ATTRIBUTE.register("cast_range_addition",
                () -> new RangedAttribute("attribute.fantazia.cast_range_addition",
                        0f, 0f, 256f));
        CAST_RANGE_TOTAL_MULTIPLIER = ATTRIBUTE.register("cast_range_total_multiplier",
                () -> new RangedAttribute("attribute.fantazia.cast_range_total_multiplier",
                        1f, 0f, 256f));

        RECHARGE_MULTIPLIER = ATTRIBUTE.register("recharge_multiplier",
                () -> new RangedAttribute("attribute.fantazia.recharge_multiplier",
                        1f,0f,256f));
        LIFESTEAL = ATTRIBUTE.register("lifesteal",
                () -> new RangedAttribute("attribute.fantazia.recharge_multiplier",
                        0f,0f,16f));
    }
}
