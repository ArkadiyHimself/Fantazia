package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class FTZAttributes extends FTZRegistry<Attribute> {
    private static final FTZAttributes INSTANCE = new FTZAttributes();

    @ObjectHolder(value = Fantazia.MODID + ":max_mana", registryName = "attribute")
    public static final RangedAttribute MAX_MANA = null;
    @ObjectHolder(value = Fantazia.MODID + ":max_stamina", registryName = "attribute")
    public static final RangedAttribute MAX_STAMINA = null;
    @ObjectHolder(value = Fantazia.MODID + ":max_stun_points", registryName = "attribute")
    public static final RangedAttribute MAX_STUN_POINTS = null;

    @ObjectHolder(value = Fantazia.MODID + ":mana_regen_multiplier", registryName = "attribute")
    public static final RangedAttribute MANA_REGEN_MULTIPLIER = null;
    @ObjectHolder(value = Fantazia.MODID + ":stamina_regen_multiplier", registryName = "attribute")
    public static final RangedAttribute STAMINA_REGEN_MULTIPLIER = null;

    @ObjectHolder(value = Fantazia.MODID + ":cast_range_addition", registryName = "attribute")
    public static final RangedAttribute CAST_RANGE_ADDITION = null;

    @ObjectHolder(value = Fantazia.MODID + ":lifesteal", registryName = "attribute")
    public static final RangedAttribute LIFESTEAL = null;
    private FTZAttributes() {
        super(ForgeRegistries.ATTRIBUTES);

        this.register("max_mana", () -> new RangedAttribute("max_mana", 20f,0f,4096f));
        this.register("max_stamina", () -> new RangedAttribute("max_stamina", 20f,0f,4096f));
        this.register("max_stun_points", () -> new RangedAttribute("max_stun_points", 200f,1f,4096f));

        this.register("mana_regen_multiplier", () -> new RangedAttribute("mana_regen_multiplier", 1f, 0f, 256f).setSyncable(true));
        this.register("stamina_regen_multiplier", () -> new RangedAttribute("stamina_regen_multiplier", 1f, 0f, 256f).setSyncable(true));

        this.register("cast_range_addition", () -> new RangedAttribute("cast_range_addition", 0f, 0f, 256f));

        this.register("lifesteal", () -> new RangedAttribute("recharge_multiplier", 0f,0f,16f));
    }
}
