package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.events.RegistryEvents;
import net.arkadiyhimself.fantazia.items.TheWorldliness;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.items.casters.DashStoneItem;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.items.expendable.AncientSparkItem;
import net.arkadiyhimself.fantazia.items.expendable.ExpendableItem;
import net.arkadiyhimself.fantazia.items.expendable.InsightBottleItem;
import net.arkadiyhimself.fantazia.items.expendable.TalentProvidingItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBladeItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.MurasamaItem;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Supplier;

public class FTZItems {
    private FTZItems() {}
    private static final DeferredRegister.Items REGISTER = DeferredRegister.Items.createItems(Fantazia.MODID);
    private static <T extends Item> DeferredHolder<Item, T> magicItem(final String name, final Supplier<T> sup) {
        DeferredHolder<Item, T> item = REGISTER.register(name, sup);
        RegistryEvents.ARTIFACTS.add(item);
        return item;
    }
    private static <T extends Item> DeferredHolder<Item, T> weaponItem(final String name, final Supplier<T> sup) {
        DeferredHolder<Item, T> item = REGISTER.register(name, sup);
        RegistryEvents.WEAPONS.add(item);
        return item;
    }
    private static <T extends Item> DeferredHolder<Item, T> expendableItem(final String name, final Supplier<T> sup) {
        DeferredHolder<Item, T> item = REGISTER.register(name, sup);
        RegistryEvents.EXPENDABLES.add(item);
        return item;
    }
    public static final DeferredHolder<Item, TheWorldliness> THE_WORLDLINESS = REGISTER.register("the_worldliness", TheWorldliness::new); // implemented and extended
    // melee weapons
    public static final DeferredHolder<Item, FragileBladeItem> FRAGILE_BLADE = weaponItem("fragile_blade", FragileBladeItem::new); // finished and implemented
    public static final DeferredHolder<Item, MurasamaItem> MURASAMA = weaponItem("murasama", MurasamaItem::new);

    // hatchets
    public static final DeferredHolder<Item, HatchetItem> WOODEN_HATCHET = weaponItem("wooden_hatchet", () -> new HatchetItem(Tiers.WOOD, -2.6f, new Item.Properties())); // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> STONE_HATCHET = weaponItem("stone_hatchet", () -> new HatchetItem(Tiers.STONE, -2.6f, new Item.Properties())); // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> IRON_HATCHET = weaponItem("iron_hatchet", () -> new HatchetItem(Tiers.IRON, -2.6f, new Item.Properties())); // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> GOLDEN_HATCHET = weaponItem("golden_hatchet", () -> new HatchetItem(Tiers.GOLD, -2.6f, new Item.Properties())); // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> DIAMOND_HATCHET = weaponItem("diamond_hatchet", () -> new HatchetItem(Tiers.DIAMOND, -2.6f, new Item.Properties())); // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> NETHERITE_HATCHET = weaponItem("netherite_hatchet", () -> new HatchetItem(Tiers.NETHERITE, -2.6f, new Item.Properties())); // finished and implemented

    // dashstones
    public static final DeferredHolder<Item, DashStoneItem> DASHSTONE1 = magicItem("dashstone1", () -> new DashStoneItem(1)); // finished and implemented
    public static final DeferredHolder<Item, DashStoneItem> DASHSTONE2 = magicItem("dashstone2", () -> new DashStoneItem(2));
    public static final DeferredHolder<Item, DashStoneItem> DASHSTONE3 = magicItem("dashstone3", () -> new DashStoneItem(3));

    // spellcasters
    public static final DeferredHolder<Item, SpellCasterItem> ENTANGLER = magicItem("entangler", () -> new SpellCasterItem(FTZSpells.ENTANGLE)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> SOUL_EATER = magicItem("soul_eater", () -> new SpellCasterItem(FTZSpells.DEVOUR)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> SCULK_HEART = magicItem("heart_of_sculk", () -> new SpellCasterItem(FTZSpells.SONIC_BOOM)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> MYSTIC_MIRROR = magicItem("mystic_mirror", () -> new SpellCasterItem(FTZSpells.REFLECT)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> BLOODLUST_AMULET = magicItem("bloodlust_amulet", () -> new SpellCasterItem(FTZSpells.DAMNED_WRATH)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> ENIGMATIC_CLOCK = magicItem("enigmatic_clock", () -> new SpellCasterItem(FTZSpells.REWIND)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> NIMBLE_DAGGER = magicItem("nimble_dagger", () -> new SpellCasterItem(FTZSpells.BOUNCE)); // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> CAUGHT_THUNDER = magicItem("caught_thunder", () -> new SpellCasterItem(FTZSpells.LIGHTNING_STRIKE));

    // auracasters
    public static final DeferredHolder<Item, AuraCasterItem> LEADERS_HORN = magicItem("leaders_horn", () -> new AuraCasterItem(FTZAuras.LEADERSHIP)); // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> TRANQUIL_HERB = magicItem("tranquil_herb", () -> new AuraCasterItem(FTZAuras.TRANQUIL)); // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> SPIRAL_NEMESIS = magicItem("spiral_nemesis", () -> new AuraCasterItem(FTZAuras.DESPAIR)); // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> ACID_BOTTLE = magicItem("acid_bottle", () -> new AuraCasterItem(FTZAuras.CORROSIVE)); // finished and implemented

    // expendables
    public static final DeferredHolder<Item, ExpendableItem> OBSCURE_ESSENCE = expendableItem("obscure_substance", () -> new ExpendableItem(Rarity.UNCOMMON)); // finished and implemented
    public static final DeferredHolder<Item, TalentProvidingItem> UNFINISHED_WINGS = expendableItem("unfinished_wings", () -> new TalentProvidingItem(Rarity.UNCOMMON, Fantazia.res("double_jump"))); // finished and implemented
    public static final DeferredHolder<Item, Item> ARACHNID_EYE = expendableItem("arachnid_eye", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64).food(Foods.arachnidEye))); // finished and implemented
    public static final DeferredHolder<Item, Item> VITALITY_FRUIT = expendableItem("vitality_fruit", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16).food(Foods.vitalityFruit))); // finished and implemented
    public static final DeferredHolder<Item, AncientSparkItem> ANCIENT_SPARK = expendableItem("ancient_spark", AncientSparkItem::new); // finished and implemented
    public static final DeferredHolder<Item, InsightBottleItem> INSIGHT_ESSENCE = expendableItem("insight_essence", InsightBottleItem::new); // finished and implemented
    public static void onRegistry(RegisterEvent event) {
        FTZBlocks.getBlockItems().forEach((block, item) -> event.register(Registries.ITEM, block, () -> item.apply(BuiltInRegistries.BLOCK.get(block))));
    }
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
    private static class Foods {
        static final FoodProperties arachnidEye = new FoodProperties.Builder().effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100), 1f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3), 1f).effect(() -> new MobEffectInstance(FTZMobEffects.DISARM, 100), 1f).alwaysEdible().nutrition(3).saturationModifier(2).build();
        static final FoodProperties vitalityFruit = new FoodProperties.Builder().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 2), 1f).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 100, 2), 1f).nutrition(5).saturationModifier(5f).alwaysEdible().build();
    }
}
