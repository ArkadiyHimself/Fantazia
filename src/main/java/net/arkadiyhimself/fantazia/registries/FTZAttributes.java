package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.simpleobjects.PercentageAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FTZAttributes {
    private static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Fantazia.MODID);
    public static final RegistryObject<Attribute> MAX_MANA = REGISTER.register("max_mana", () -> new RangedAttribute("fantazia.attribute.max_mana", 20,0,4096).setSyncable(true));
    public static final RegistryObject<Attribute> MAX_STAMINA = REGISTER.register("max_stamina", () -> new RangedAttribute("fantazia.attribute.max_stamina", 20,0f,4096).setSyncable(true));
    public static final RegistryObject<Attribute> MAX_STUN_POINTS = REGISTER.register("max_stun_points", () -> new RangedAttribute("fantazia.attribute.max_stun_points", 300,1,4096));
    public static final RegistryObject<Attribute> MANA_REGEN_MULTIPLIER = REGISTER.register("mana_regen_multiplier", () -> new RangedAttribute("fantazia.attribute.mana_regen_multiplier", 1, 0, 256).setSyncable(true));
    public static final RegistryObject<Attribute> STAMINA_REGEN_MULTIPLIER = REGISTER.register("stamina_regen_multiplier", () -> new RangedAttribute("fantazia.attribute.stamina_regen_multiplier", 1, 0, 256).setSyncable(true));
    public static final RegistryObject<Attribute> CAST_RANGE_ADDITION = REGISTER.register("cast_range_addition", () -> new RangedAttribute("fantazia.attribute.cast_range_addition", 0, 0f, 256));
    public static final RegistryObject<Attribute> LIFESTEAL = REGISTER.register("lifesteal", () -> new RangedAttribute("fantazia.attribute.recharge_multiplier", 0,0,16));
    public static final RegistryObject<Attribute> EVASION = REGISTER.register("evasion", () -> new PercentageAttribute("fantazia.attribute.evasion",0).setSyncable(true));
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
