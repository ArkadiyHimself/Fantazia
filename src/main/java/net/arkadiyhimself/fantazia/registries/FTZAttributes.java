package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.simpleobjects.PercentageAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZAttributes {
    private FTZAttributes() {}
    private static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(Registries.ATTRIBUTE, Fantazia.MODID);
    public static final DeferredHolder<Attribute, Attribute> MAX_MANA = REGISTER.register("max_mana", () -> new RangedAttribute("fantazia.attribute.max_mana", 20,0,4096).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAX_STAMINA = REGISTER.register("max_stamina", () ->  new RangedAttribute("fantazia.attribute.max_stamina", 20,0f,4096).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAX_STUN_POINTS = REGISTER.register("max_stun_points", () -> new RangedAttribute("fantazia.attribute.max_stun_points", 300,1,4096).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MANA_REGEN_MULTIPLIER = REGISTER.register("mana_regen_multiplier", () -> new RangedAttribute("fantazia.attribute.mana_regen_multiplier", 1, 0, 256).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> STAMINA_REGEN_MULTIPLIER = REGISTER.register("stamina_regen_multiplier", () -> new RangedAttribute("fantazia.attribute.stamina_regen_multiplier", 1, 0, 256).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> CAST_RANGE_ADDITION = REGISTER.register("cast_range_addition", () -> new RangedAttribute("fantazia.attribute.cast_range_addition", 0, 0f, 256));
    public static final DeferredHolder<Attribute, Attribute> LIFESTEAL = REGISTER.register("lifesteal", () -> new RangedAttribute("fantazia.attribute.lifesteal", 0,0,16));
    public static final DeferredHolder<Attribute, PercentageAttribute> EVASION = REGISTER.register("evasion", () -> new PercentageAttribute("fantazia.attribute.evasion",0).setSyncable(true));
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
