package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
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
import net.minecraft.core.Holder;
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

    private static DeferredHolder<Item, SpellCasterItem> spellCaster(String id, Holder<AbstractSpell> spellHolder) {
        return magicItem(id, () -> new SpellCasterItem(spellHolder));
    }

    private static DeferredHolder<Item, AuraCasterItem> auraCaster(String id, Holder<BasicAura<?>> auraHolder) {
        return magicItem(id, () -> new AuraCasterItem(auraHolder));
    }

    public static final DeferredHolder<Item, TheWorldliness> THE_WORLDLINESS; // implemented and extended

    // melee weapons
    public static final DeferredHolder<Item, FragileBladeItem> FRAGILE_BLADE; // finished and implemented
    public static final DeferredHolder<Item, MurasamaItem> MURASAMA;

    // hatchets
    public static final DeferredHolder<Item, HatchetItem> WOODEN_HATCHET; // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> STONE_HATCHET; // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> IRON_HATCHET; // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> GOLDEN_HATCHET; // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> DIAMOND_HATCHET; // finished and implemented
    public static final DeferredHolder<Item, HatchetItem> NETHERITE_HATCHET; // finished and implemented

    // dashstones
    public static final DeferredHolder<Item, DashStoneItem> DASHSTONE1; // finished and implemented
    public static final DeferredHolder<Item, DashStoneItem> DASHSTONE2; // finished and implemented
    public static final DeferredHolder<Item, DashStoneItem> DASHSTONE3;

    // spellcasters
    public static final DeferredHolder<Item, SpellCasterItem> ENTANGLER; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> ENIGMATIC_CLOCK; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> ATHAME; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> SANDMANS_DUST; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> CARD_DECK; // finished and implemented

    public static final DeferredHolder<Item, SpellCasterItem> SOUL_EATER; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> SCULK_HEART; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> NIMBLE_DAGGER; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> CAUGHT_THUNDER; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> PUPPET_DOLL; // finished and implemented

    public static final DeferredHolder<Item, SpellCasterItem> MYSTIC_MIRROR; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> BLOODLUST_AMULET; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> CONTAINED_SOUND; // finished and implemented
    public static final DeferredHolder<Item, SpellCasterItem> WITHERS_QUINTESSENCE; // finished and implemented

    // auracasters
    public static final DeferredHolder<Item, AuraCasterItem> LEADERS_HORN; // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> TRANQUIL_HERB; // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> SPIRAL_NEMESIS; // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> ACID_BOTTLE; // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> NETHER_HEART; // finished and implemented
    public static final DeferredHolder<Item, AuraCasterItem> AMPLIFIED_ICE; // finished and implemented

    // expendables
    public static final DeferredHolder<Item, ExpendableItem> OBSCURE_SUBSTANCE; // finished and implemented
    public static final DeferredHolder<Item, TalentProvidingItem> UNFINISHED_WINGS; // finished and implemented
    public static final DeferredHolder<Item, Item> ARACHNID_EYE; // finished and implemented
    public static final DeferredHolder<Item, Item> VITALITY_FRUIT; // finished and implemented
    public static final DeferredHolder<Item, AncientSparkItem> ANCIENT_SPARK; // finished and implemented
    public static final DeferredHolder<Item, InsightBottleItem> INSIGHT_ESSENCE; // finished and implemented

    static {
        THE_WORLDLINESS = REGISTER.register("the_worldliness", TheWorldliness::new);
        FRAGILE_BLADE = weaponItem("fragile_blade", FragileBladeItem::new);
        MURASAMA = weaponItem("murasama", MurasamaItem::new);

        WOODEN_HATCHET = weaponItem("wooden_hatchet", () -> new HatchetItem(Tiers.WOOD, -2.6f, new Item.Properties()));
        STONE_HATCHET = weaponItem("stone_hatchet", () -> new HatchetItem(Tiers.STONE, -2.6f, new Item.Properties()));
        IRON_HATCHET = weaponItem("iron_hatchet", () -> new HatchetItem(Tiers.IRON, -2.6f, new Item.Properties()));
        GOLDEN_HATCHET = weaponItem("golden_hatchet", () -> new HatchetItem(Tiers.GOLD, -2.6f, new Item.Properties()));
        DIAMOND_HATCHET = weaponItem("diamond_hatchet", () -> new HatchetItem(Tiers.DIAMOND, -2.6f, new Item.Properties()));
        NETHERITE_HATCHET = weaponItem("netherite_hatchet", () -> new HatchetItem(Tiers.NETHERITE, -2.6f, new Item.Properties()));

        DASHSTONE1 = magicItem("dashstone1", () -> new DashStoneItem(1));
        DASHSTONE2 = magicItem("dashstone2", () -> new DashStoneItem(2));
        DASHSTONE3 = magicItem("dashstone3", () -> new DashStoneItem(3));

        ENTANGLER = spellCaster("entangler", FTZSpells.ENTANGLE);
        ENIGMATIC_CLOCK = spellCaster("enigmatic_clock", FTZSpells.REWIND);
        ATHAME = spellCaster("athame", FTZSpells.TRANSFER);
        SANDMANS_DUST = spellCaster("sandmans_dust", FTZSpells.VANISH);
        CARD_DECK = spellCaster("card_deck", FTZSpells.ALL_IN);

        SOUL_EATER = spellCaster("soul_eater", FTZSpells.DEVOUR);
        SCULK_HEART = spellCaster("heart_of_sculk", FTZSpells.SONIC_BOOM);
        NIMBLE_DAGGER = spellCaster("nimble_dagger", FTZSpells.BOUNCE);
        CAUGHT_THUNDER = spellCaster("caught_thunder", FTZSpells.LIGHTNING_STRIKE);
        PUPPET_DOLL = spellCaster("puppet_doll", FTZSpells.PUPPETEER);

        MYSTIC_MIRROR = spellCaster("mystic_mirror", FTZSpells.REFLECT);
        BLOODLUST_AMULET = spellCaster("bloodlust_amulet", FTZSpells.DAMNED_WRATH);
        CONTAINED_SOUND = spellCaster("contained_sound", FTZSpells.SHOCKWAVE);
        WITHERS_QUINTESSENCE = spellCaster("withers_quintessence", FTZSpells.SUSTAIN);

        LEADERS_HORN = auraCaster("leaders_horn", FTZAuras.LEADERSHIP);
        TRANQUIL_HERB = auraCaster("tranquil_herb", FTZAuras.TRANQUIL);
        SPIRAL_NEMESIS = auraCaster("spiral_nemesis", FTZAuras.DESPAIR);
        ACID_BOTTLE = auraCaster("acid_bottle", FTZAuras.CORROSIVE);
        NETHER_HEART = auraCaster("nether_heart", FTZAuras.HELLFIRE);
        AMPLIFIED_ICE = auraCaster("amplified_ice", FTZAuras.FROSTBITE);

        OBSCURE_SUBSTANCE = expendableItem("obscure_substance", () -> new ExpendableItem(Rarity.UNCOMMON));
        UNFINISHED_WINGS = expendableItem("unfinished_wings", () -> new TalentProvidingItem(Rarity.UNCOMMON, Fantazia.res("double_jump")));
        ARACHNID_EYE = expendableItem("arachnid_eye", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64).food(Foods.arachnidEye)));
        VITALITY_FRUIT = expendableItem("vitality_fruit", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16).food(Foods.vitalityFruit)));
        ANCIENT_SPARK = expendableItem("ancient_spark", AncientSparkItem::new);
        INSIGHT_ESSENCE = expendableItem("insight_essence", InsightBottleItem::new);
    }

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
