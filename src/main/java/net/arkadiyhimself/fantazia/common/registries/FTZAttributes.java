package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.util.simpleobjects.PercentageAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface FTZAttributes {
    
    DeferredRegister<Attribute> REGISTER = DeferredRegister.create(Registries.ATTRIBUTE, Fantazia.MODID);
    
    DeferredHolder<Attribute, Attribute> MAX_MANA = REGISTER.register("max_mana", () -> new RangedAttribute("attribute.fantazia.max_mana", 20,0,4096).setSyncable(true));
    DeferredHolder<Attribute, Attribute> MAX_STAMINA = REGISTER.register("max_stamina", () ->  new RangedAttribute("attribute.fantazia.max_stamina", 20,0,4096).setSyncable(true));
    DeferredHolder<Attribute, Attribute> MAX_STUN_POINTS = REGISTER.register("max_stun_points", () -> new RangedAttribute("attribute.fantazia.max_stun_points", 300,10,4096).setSyncable(true));
    DeferredHolder<Attribute, Attribute> CAST_RANGE_ADDITION = REGISTER.register("cast_range_addition", () -> new RangedAttribute("attribute.fantazia.cast_range_addition", 0, -256, 256).setSyncable(true));
    DeferredHolder<Attribute, Attribute> LIFESTEAL = REGISTER.register("lifesteal", () -> new RangedAttribute("attribute.fantazia.lifesteal", 0,0,16));
    DeferredHolder<Attribute, PercentageAttribute> EVASION = REGISTER.register("evasion", () -> new PercentageAttribute("attribute.fantazia.evasion",0).setSyncable(true));
    DeferredHolder<Attribute, PercentageAttribute> RECHARGE_MULTIPLIER = REGISTER.register("recharge_multiplier", () -> new PercentageAttribute("attribute.fantazia.recharge_multiplier", 100).setSyncable(true));
    DeferredHolder<Attribute, Attribute> AURA_RANGE_ADDITION = REGISTER.register("aura_range_addition", () -> new RangedAttribute("attribute.fantazia.aura_range_addition", 0, -256, 256).setSyncable(true));

    static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
