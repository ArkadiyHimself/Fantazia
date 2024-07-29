package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class FTZEnchantments extends FTZRegistry<Enchantment> {
    public static FTZEnchantments INSTANCE = new FTZEnchantments();
    @ObjectHolder(value = Fantazia.MODID + ":disintegration", registryName = "enchantment")
    public static final DisintegrationEnchantment DISINTEGRATION = null;

    @ObjectHolder(value = Fantazia.MODID + ":ice_aspect", registryName = "enchantment")
    public static final IceAspectEnchantment ICE_ASPECT = null;

    @ObjectHolder(value = Fantazia.MODID + ":freeze", registryName = "enchantment")
    public static final FreezeEnchantment FREEZE = null;

    @ObjectHolder(value = Fantazia.MODID + ":decisive_strike", registryName = "enchantment")
    public static final DecisiveStrikeEnchantment DECISIVE_STRIKE = null;

    @ObjectHolder(value = Fantazia.MODID + ":bully", registryName = "enchantment")
    public static final BullyEnchantment BULLY = null;

    @ObjectHolder(value = Fantazia.MODID + ":duelist", registryName = "enchantment")
    public static final CrossbowDamageEnchantment DUELIST = null;

    @ObjectHolder(value = Fantazia.MODID + ":ballista", registryName = "enchantment")
    public static final CrossbowDamageEnchantment BALLISTA = null;

    //  hatchet enchantments
    @ObjectHolder(value = Fantazia.MODID + ":phasing", registryName = "enchantment")
    public static final PhasingEnchantment PHASING = null;

    @ObjectHolder(value = Fantazia.MODID + ":ricochet", registryName = "enchantment")
    public static final RicochetEnchantment RICOCHET = null;

    @ObjectHolder(value = Fantazia.MODID + ":headshot", registryName = "enchantment")
    public static final HeadshotEnchantment HEADSHOT = null;

    private FTZEnchantments() {
        super(ForgeRegistries.ENCHANTMENTS);

        this.register("disintegration", DisintegrationEnchantment::new);
        this.register("ice_aspect", IceAspectEnchantment::new);
        this.register("freeze", FreezeEnchantment::new);
        this.register("decisive_strike", DecisiveStrikeEnchantment::new);
        this.register("bully", BullyEnchantment::new);
        this.register("duelist", CrossbowDamageEnchantment::new);
        this.register("ballista", CrossbowDamageEnchantment::new);

        this.register("phasing", PhasingEnchantment::new);
        this.register("ricochet", RicochetEnchantment::new);
        this.register("headshot", HeadshotEnchantment::new);
    }
}
