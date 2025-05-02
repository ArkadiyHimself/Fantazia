package net.arkadiyhimself.fantazia.registries;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.entities.CustomBoat;
import net.arkadiyhimself.fantazia.events.RegistryEvents;
import net.arkadiyhimself.fantazia.items.FantazicPaintingItem;
import net.arkadiyhimself.fantazia.items.TheWorldliness;
import net.arkadiyhimself.fantazia.items.WisdomCatcherItem;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.items.casters.DashStoneItem;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.items.expendable.*;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBladeItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.MurasamaItem;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FTZItems {
    private FTZItems() {}

    private static final DeferredRegister.Items REGISTER = DeferredRegister.Items.createItems(Fantazia.MODID);

    public static final List<DeferredItem<Item>> SIMPLE_ITEMS = Lists.newArrayList();
    public static final List<DeferredItem<SpellCasterItem>> SPELL_CASTERS = Lists.newArrayList();
    public static final List<DeferredItem<AuraCasterItem>> AURA_CASTERS = Lists.newArrayList();
    public static final List<DeferredItem<DashStoneItem>> DASHSTONES = Lists.newArrayList();
    public static final List<DeferredItem<HatchetItem>> HATCHETS = Lists.newArrayList();
    public static final List<DeferredItem<?>> WEAPONS = Lists.newArrayList();

    private static <T extends Item> DeferredItem<T> registerItem(final String name, final Supplier<T> sup, @Nullable List<DeferredItem<? extends Item>> tabList, @Nullable List<DeferredItem<T>> modelList) {
        DeferredItem<T> item = REGISTER.register(name, sup);
        if (tabList != null) tabList.add(item);
        if (modelList != null) modelList.add(item);
        return item;
    }

    private static <T extends Item> DeferredItem<T> magicItem(final String name, final Supplier<T> sup, @Nullable List<DeferredItem<T>> modelList)  {
        return registerItem(name, sup, RegistryEvents.ARTIFACTS, modelList);
    }

    private static <T extends Item> DeferredItem<T> weaponItem(final String name, final Supplier<T> sup, @Nullable List<DeferredItem<T>> modelList) {
        return registerItem(name, sup, RegistryEvents.WEAPONS, modelList);
    }
    private static <T extends Item> DeferredItem<T> expendableItem(final String name, final Supplier<T> sup, @Nullable List<DeferredItem<T>> modelList) {
        return registerItem(name, sup, RegistryEvents.EXPENDABLES, modelList);
    }
    private static <T extends Item> DeferredItem<T> blockItem(final String name, final Supplier<T> sup, @Nullable List<DeferredItem<T>> modelList) {
        return registerItem(name, sup, RegistryEvents.BLOCK_ITEMS, modelList);
    }

    private static DeferredItem<SpellCasterItem> spellCaster(String id, Holder<AbstractSpell> spellHolder) {
        return magicItem(id, () -> new SpellCasterItem(spellHolder), SPELL_CASTERS);
    }

    private static DeferredItem<AuraCasterItem> auraCaster(String id, Holder<BasicAura> auraHolder) {
        return magicItem(id, () -> new AuraCasterItem(auraHolder),  AURA_CASTERS);
    }

    private static DeferredItem<DashStoneItem> dashStone(String id, int lvl) {
        return magicItem(id, () -> new DashStoneItem(lvl), DASHSTONES);
    }

    public static final DeferredItem<TheWorldliness> THE_WORLDLINESS; // implemented and extended
    public static final DeferredItem<WisdomCatcherItem> WISDOM_CATCHER; // implemented and extended

    // melee weapons
    public static final DeferredItem<FragileBladeItem> FRAGILE_BLADE; // finished and implemented
    public static final DeferredItem<MurasamaItem> MURASAMA;

    // hatchets
    public static final DeferredItem<HatchetItem> WOODEN_HATCHET; // finished and implemented
    public static final DeferredItem<HatchetItem> STONE_HATCHET; // finished and implemented
    public static final DeferredItem<HatchetItem> IRON_HATCHET; // finished and implemented
    public static final DeferredItem<HatchetItem> GOLDEN_HATCHET; // finished and implemented
    public static final DeferredItem<HatchetItem> DIAMOND_HATCHET; // finished and implemented
    public static final DeferredItem<HatchetItem> NETHERITE_HATCHET; // finished and implemented

    // dashstones
    public static final DeferredItem<DashStoneItem> DASHSTONE1; // finished and implemented
    public static final DeferredItem<DashStoneItem> DASHSTONE2; // finished and implemented
    public static final DeferredItem<DashStoneItem> DASHSTONE3;

    // spellcasters
    public static final DeferredItem<SpellCasterItem> ENTANGLER; // finished and implemented
    public static final DeferredItem<SpellCasterItem> ENIGMATIC_CLOCK; // finished and implemented
    public static final DeferredItem<SpellCasterItem> ATHAME; // finished and implemented
    public static final DeferredItem<SpellCasterItem> SANDMANS_DUST; // finished and implemented
    public static final DeferredItem<SpellCasterItem> CARD_DECK; // finished and implemented
    public static final DeferredItem<SpellCasterItem> ROAMERS_COMPASS;

    public static final DeferredItem<SpellCasterItem> SOUL_EATER; // finished and implemented
    public static final DeferredItem<SpellCasterItem> SCULK_HEART; // finished and implemented
    public static final DeferredItem<SpellCasterItem> NIMBLE_DAGGER; // finished and implemented
    public static final DeferredItem<SpellCasterItem> CAUGHT_THUNDER; // finished and implemented
    public static final DeferredItem<SpellCasterItem> PUPPET_DOLL; // finished and implemented
    public static final DeferredItem<SpellCasterItem> BROKEN_STAFF;

    public static final DeferredItem<SpellCasterItem> MYSTIC_MIRROR; // finished and implemented
    public static final DeferredItem<SpellCasterItem> BLOODLUST_AMULET; // finished and implemented
    public static final DeferredItem<SpellCasterItem> CONTAINED_SOUND; // finished and implemented
    public static final DeferredItem<SpellCasterItem> WITHERS_QUINTESSENCE; // finished and implemented
    public static final DeferredItem<SpellCasterItem> RUSTY_RING; // finished and implemented

    // auracasters
    public static final DeferredItem<AuraCasterItem> LEADERS_HORN; // finished and implemented
    public static final DeferredItem<AuraCasterItem> TRANQUIL_HERB; // finished and implemented
    public static final DeferredItem<AuraCasterItem> SPIRAL_NEMESIS; // finished and implemented
    public static final DeferredItem<AuraCasterItem> ACID_BOTTLE; // finished and implemented
    public static final DeferredItem<AuraCasterItem> NETHER_HEART; // finished and implemented
    public static final DeferredItem<AuraCasterItem> AMPLIFIED_ICE; // finished and implemented
    public static final DeferredItem<AuraCasterItem> OPTICAL_LENS; // finished and implemented
    public static final DeferredItem<AuraCasterItem> NECKLACE_OF_CLAIRVOYANCE; // finished and implemented

    // expendables
    public static final DeferredItem<ExpendableItem> OBSCURE_SUBSTANCE; // finished and implemented
    public static final DeferredItem<TalentProvidingItem> UNFINISHED_WINGS; // finished and implemented
    public static final DeferredItem<Item> ARACHNID_EYE; // finished and implemented
    public static final DeferredItem<Item> VITALITY_FRUIT; // finished and implemented
    public static final DeferredItem<AncientSparkItem> ANCIENT_SPARK; // finished and implemented
    public static final DeferredItem<InsightBottleItem> INSIGHT_ESSENCE; // finished and implemented
    public static final DeferredItem<Item> RAW_FANTAZIUM; // finished and implemented
    public static final DeferredItem<Item> FANTAZIUM_INGOT; // finished and implemented
    public static final DeferredItem<FantazicPaintingItem> FANTAZIC_PAINTING; // finished and implemented

    // obscure wood items
    public static final DeferredItem<SignItem> OBSCURE_SIGN; // finished and implemented
    public static final DeferredItem<HangingSignItem> OBSCURE_HANGING_SIGN; // finished and implemented
    public static final DeferredItem<CustomBoatItem> OBSCURE_BOAT; // finished and implemented
    public static final DeferredItem<CustomBoatItem> OBSCURE_CHEST_BOAT; // finished and implemented

    public static void onRegistry(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM)) return;
        for (Map.Entry<ResourceLocation, FTZBlocks.BlockItemSupplier> entry : FTZBlocks.getBlockItems().entrySet()) {
            BlockItem blockItem = entry.getValue().apply(BuiltInRegistries.BLOCK.get(entry.getKey()));
            if (blockItem != null) event.register(Registries.ITEM, entry.getKey(), () -> blockItem);
        }
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    private static Supplier<Item> basicAssItem() {
        return () -> new Item(new Item.Properties());
    }

    static {
        THE_WORLDLINESS = REGISTER.register("the_worldliness", TheWorldliness::new);
        WISDOM_CATCHER = magicItem("wisdom_catcher", WisdomCatcherItem::new,null);
        FRAGILE_BLADE = weaponItem("fragile_blade", FragileBladeItem::new,null);
        MURASAMA = weaponItem("murasama", MurasamaItem::new,null);

        WOODEN_HATCHET = weaponItem("wooden_hatchet", () -> new HatchetItem(Tiers.WOOD, -2.6f), HATCHETS);
        STONE_HATCHET = weaponItem("stone_hatchet", () -> new HatchetItem(Tiers.STONE, -2.6f), HATCHETS);
        IRON_HATCHET = weaponItem("iron_hatchet", () -> new HatchetItem(Tiers.IRON, -2.6f), HATCHETS);
        GOLDEN_HATCHET = weaponItem("golden_hatchet", () -> new HatchetItem(Tiers.GOLD, -2.6f), HATCHETS);
        DIAMOND_HATCHET = weaponItem("diamond_hatchet", () -> new HatchetItem(Tiers.DIAMOND, -2.6f), HATCHETS);
        NETHERITE_HATCHET = weaponItem("netherite_hatchet", () -> new HatchetItem(Tiers.NETHERITE, -2.6f), HATCHETS);

        DASHSTONE1 = dashStone("dashstone1",1);
        DASHSTONE2 = dashStone("dashstone2",2);
        DASHSTONE3 = dashStone("dashstone3",3);

        ENTANGLER = spellCaster("entangler", FTZSpells.ENTANGLE);
        ENIGMATIC_CLOCK = spellCaster("enigmatic_clock", FTZSpells.REWIND);
        ATHAME = spellCaster("athame", FTZSpells.TRANSFER);
        SANDMANS_DUST = spellCaster("sandmans_dust", FTZSpells.VANISH);
        CARD_DECK = spellCaster("card_deck", FTZSpells.ALL_IN);
        ROAMERS_COMPASS = spellCaster("roamers_compass", FTZSpells.WANDERERS_SPIRIT);

        SOUL_EATER = spellCaster("soul_eater", FTZSpells.DEVOUR);
        SCULK_HEART = spellCaster("heart_of_sculk", FTZSpells.SONIC_BOOM);
        NIMBLE_DAGGER = spellCaster("nimble_dagger", FTZSpells.BOUNCE);
        CAUGHT_THUNDER = spellCaster("caught_thunder", FTZSpells.LIGHTNING_STRIKE);
        PUPPET_DOLL = spellCaster("puppet_doll", FTZSpells.PUPPETEER);
        BROKEN_STAFF = spellCaster("broken_staff", FTZSpells.KNOCK_OUT);

        MYSTIC_MIRROR = spellCaster("mystic_mirror", FTZSpells.REFLECT);
        BLOODLUST_AMULET = spellCaster("bloodlust_amulet", FTZSpells.DAMNED_WRATH);
        CONTAINED_SOUND = spellCaster("contained_sound", FTZSpells.SHOCKWAVE);
        WITHERS_QUINTESSENCE = spellCaster("withers_quintessence", FTZSpells.SUSTAIN);
        RUSTY_RING = spellCaster("rusty_ring", FTZSpells.REINFORCE);

        LEADERS_HORN = auraCaster("leaders_horn", FTZAuras.LEADERSHIP);
        TRANQUIL_HERB = auraCaster("tranquil_herb", FTZAuras.TRANQUIL);
        SPIRAL_NEMESIS = auraCaster("spiral_nemesis", FTZAuras.DESPAIR);
        ACID_BOTTLE = auraCaster("acid_bottle", FTZAuras.CORROSIVE);
        NETHER_HEART = auraCaster("nether_heart", FTZAuras.HELLFIRE);
        AMPLIFIED_ICE = auraCaster("amplified_ice", FTZAuras.FROSTBITE);
        OPTICAL_LENS = auraCaster("optical_lens", FTZAuras.DIFFRACTION);
        NECKLACE_OF_CLAIRVOYANCE = auraCaster("necklace_of_clairvoyance", FTZAuras.UNCOVER);

        OBSCURE_SUBSTANCE = expendableItem("obscure_substance", () -> new ExpendableItem(Rarity.UNCOMMON),null);
        UNFINISHED_WINGS = expendableItem("unfinished_wings", () -> new TalentProvidingItem(Rarity.UNCOMMON, Fantazia.res("double_jump")),null);
        ARACHNID_EYE = expendableItem("arachnid_eye", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(64).food(FTZFoods.ARACHNID_EYE)),null);
        VITALITY_FRUIT = expendableItem("vitality_fruit", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16).food(FTZFoods.VITALITY_FRUIT)),null);
        ANCIENT_SPARK = expendableItem("ancient_spark", AncientSparkItem::new,null);
        INSIGHT_ESSENCE = expendableItem("insight_essence", InsightBottleItem::new,null);
        RAW_FANTAZIUM = expendableItem("raw_fantazium", basicAssItem(), SIMPLE_ITEMS);
        FANTAZIUM_INGOT = expendableItem("fantazium_ingot", basicAssItem(), SIMPLE_ITEMS);
        FANTAZIC_PAINTING = expendableItem("fantazic_painting", FantazicPaintingItem::new,null);

        OBSCURE_SIGN = blockItem("obscure_sign", () -> new SignItem(new Item.Properties().stacksTo(16), FTZBlocks.OBSCURE_SIGN.get(), FTZBlocks.OBSCURE_WALL_SIGN.get()),null);
        OBSCURE_HANGING_SIGN = blockItem("obscure_hanging_sign", () -> new HangingSignItem(FTZBlocks.OBSCURE_HANGING_SIGN.get(), FTZBlocks.OBSCURE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)),null);
        OBSCURE_BOAT = blockItem("obscure_boat", () -> new CustomBoatItem(false, CustomBoat.Type.OBSCURE, new Item.Properties().stacksTo(1)),null);
        OBSCURE_CHEST_BOAT = blockItem("obscure_chest_boat", () -> new CustomBoatItem(true, CustomBoat.Type.OBSCURE, new Item.Properties().stacksTo(1)),null);
    }



}
