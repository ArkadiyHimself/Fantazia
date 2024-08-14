package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class FTZAttributes extends FTZRegistry<Attribute> {
    @SuppressWarnings("unused")
    private static final FTZAttributes INSTANCE = new FTZAttributes();

    @ObjectHolder(value = Fantazia.MODID + ":max_mana", registryName = "attribute")
    public static final RangedAttribute MAX_MANA = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":max_stamina", registryName = "attribute")
    public static final RangedAttribute MAX_STAMINA = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":max_stun_points", registryName = "attribute")
    public static final RangedAttribute MAX_STUN_POINTS = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":mana_regen_multiplier", registryName = "attribute")
    public static final RangedAttribute MANA_REGEN_MULTIPLIER = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":stamina_regen_multiplier", registryName = "attribute")
    public static final RangedAttribute STAMINA_REGEN_MULTIPLIER = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":cast_range_addition", registryName = "attribute")
    public static final RangedAttribute CAST_RANGE_ADDITION = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal", registryName = "attribute")
    public static final RangedAttribute LIFESTEAL = null; // implemented
    @ObjectHolder(value = Fantazia.MODID + ":evasion", registryName = "attribute")
    public static final RangedAttribute EVASION = null;
    private FTZAttributes() {
        super(ForgeRegistries.ATTRIBUTES);
        this.register("max_mana", () -> new RangedAttribute("fantazia.attribute.max_mana", 20,0,4096).setSyncable(true));
        this.register("max_stamina", () -> new RangedAttribute("fantazia.attribute.max_stamina", 20,0f,4096).setSyncable(true));
        this.register("max_stun_points", () -> new RangedAttribute("fantazia.attribute.max_stun_points", 200,1,4096));
        this.register("mana_regen_multiplier", () -> new RangedAttribute("fantazia.attribute.mana_regen_multiplier", 1, 0, 256).setSyncable(true));
        this.register("stamina_regen_multiplier", () -> new RangedAttribute("fantazia.attribute.stamina_regen_multiplier", 1, 0, 256).setSyncable(true));
        this.register("cast_range_addition", () -> new RangedAttribute("fantazia.attribute.cast_range_addition", 0, 0f, 256));
        this.register("lifesteal", () -> new RangedAttribute("fantazia.attribute.recharge_multiplier", 0,0,16));
        this.register("evasion", () -> new RangedAttribute("fantazia.attribute.evasion",0,0,1).setSyncable(true));
    }
}
