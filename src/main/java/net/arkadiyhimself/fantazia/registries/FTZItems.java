package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.events.RegistryEvents;
import net.arkadiyhimself.fantazia.items.casters.AuraCaster;
import net.arkadiyhimself.fantazia.items.casters.DashStone;
import net.arkadiyhimself.fantazia.items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.items.expendable.ObscureSubstance;
import net.arkadiyhimself.fantazia.items.expendable.TalentProvidingItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBlade;
import net.arkadiyhimself.fantazia.items.weapons.Melee.Murasama;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FTZItems {
    private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Fantazia.MODID);
    private static RegistryObject<Item> magicItem(final String name, final Supplier<Item> sup) {
        RegistryObject<Item> item = REGISTER.register(name, sup);
        RegistryEvents.MAGIC_ITEM.add(item);
        return item;
    }
    private static RegistryObject<Item> weaponItem(final String name, final Supplier<Item> sup) {
        RegistryObject<Item> item = REGISTER.register(name, sup);
        RegistryEvents.WEAPON_ITEM.add(item);
        return item;
    }
    private static RegistryObject<Item> expendableItem(final String name, final Supplier<Item> sup) {
        RegistryObject<Item> item = REGISTER.register(name, sup);
        RegistryEvents.EXPENDABLE_ITEM.add(item);
        return item;
    }
    public static final RegistryObject<Item> FRAGILE_BLADE = weaponItem("fragile_blade", FragileBlade::new); // finished and implemented
    public static final RegistryObject<Item> MURASAMA = weaponItem("murasama", Murasama::new);
    public static final RegistryObject<Item> WOODEN_HATCHET = weaponItem("wooden_hatchet", () -> new HatchetItem(Tiers.WOOD, -2.6f, new Item.Properties())); // finished and implemented
    public static final RegistryObject<Item> STONE_HATCHET = weaponItem("stone_hatchet", () -> new HatchetItem(Tiers.STONE, -2.6f, new Item.Properties())); // finished and implemented
    public static final RegistryObject<Item> IRON_HATCHET = weaponItem("iron_hatchet", () -> new HatchetItem(Tiers.IRON, -2.6f, new Item.Properties())); // finished and implemented
    public static final RegistryObject<Item> GOLDEN_HATCHET = weaponItem("golden_hatchet", () -> new HatchetItem(Tiers.GOLD, -2.6f, new Item.Properties())); // finished and implemented
    public static final RegistryObject<Item> DIAMOND_HATCHET = weaponItem("diamond_hatchet", () -> new HatchetItem(Tiers.DIAMOND, -2.6f, new Item.Properties())); // finished and implemented
    public static final RegistryObject<Item> NETHERITE_HATCHET = weaponItem("netherite_hatchet", () -> new HatchetItem(Tiers.NETHERITE, -2.6f, new Item.Properties())); // finished and implemented
    public static final RegistryObject<Item> DASHSTONE1 = magicItem("dashstone1", () -> new DashStone(1)); // finished and implemented
    public static final RegistryObject<Item> DASHSTONE2 = magicItem("dashstone2", () -> new DashStone(2));
    public static final RegistryObject<Item> DASHSTONE3 = magicItem("dashstone3", () -> new DashStone(3));
    public static final RegistryObject<Item> ENTANGLER = magicItem("entangler", () -> new SpellCaster(FTZSpells.ENTANGLE)); // finished and implemented
    public static final RegistryObject<Item> SOUL_EATER = magicItem("soul_eater", () -> new SpellCaster(FTZSpells.DEVOUR)); // finished and implemented
    public static final RegistryObject<Item> SCULK_HEART = magicItem("heart_of_sculk", () -> new SpellCaster(FTZSpells.SONIC_BOOM)); // finished and implemented
    public static final RegistryObject<Item> MYSTIC_MIRROR = magicItem("mystic_mirror", () -> new SpellCaster(FTZSpells.REFLECT)); // finished and implemented
    public static final RegistryObject<Item> BLOODLUST_AMULET = magicItem("bloodlust_amulet", () -> new SpellCaster(FTZSpells.DAMNED_WRATH)); // finished and implemented
    public static final RegistryObject<Item> LEADERS_HORN = magicItem("leaders_horn", () -> new AuraCaster(FTZAuras.LEADERSHIP)); // finished and implemented
    public static final RegistryObject<Item> TRANQUIL_HERB = magicItem("tranquil_herb", () -> new AuraCaster(FTZAuras.TRANQUIL)); // finished and implemented
    public static final RegistryObject<Item> SPIRAL_NEMESIS = magicItem("spiral_nemesis", () -> new AuraCaster(FTZAuras.DESPAIR));
    public static final RegistryObject<Item> OBSCURE_ESSENCE = expendableItem("obscure_substance", () -> new ObscureSubstance(Rarity.UNCOMMON)); // finished and implemented
    public static final RegistryObject<Item> UNFINISHED_WINGS = expendableItem("unfinished_wings", () -> new TalentProvidingItem(Rarity.UNCOMMON, Fantazia.res("double_jump"))); // finished and implemented
    public static final RegistryObject<Item> ARACHNID_EYE = expendableItem("arachnid_eye", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64).food(Foods.ARACHNID_EYE))); // finished and implemented
    public static final RegistryObject<Item> VITALITY_FRUIT = expendableItem("vitality_fruit", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16).food(Foods.VITALITY_FRUIT))); // finished and implemented

    public static void onRegistry(RegisterEvent event) {
        FTZBlocks.getBlockItems().forEach((block, item) -> event.register(ForgeRegistries.Keys.ITEMS, block, () -> item.apply(ForgeRegistries.BLOCKS.getValue(block))));
    }
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    private static class Foods {
        static FoodProperties ARACHNID_EYE = new FoodProperties.Builder().effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100), 1f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3), 1f).effect(() -> new MobEffectInstance(FTZMobEffects.DISARM.get(), 100), 1f).alwaysEat().nutrition(3).saturationMod(2).build();
        static FoodProperties VITALITY_FRUIT = new FoodProperties.Builder().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 2), 1f).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 100, 2), 1f).nutrition(5).saturationMod(5f).alwaysEat().build();
    }
}
