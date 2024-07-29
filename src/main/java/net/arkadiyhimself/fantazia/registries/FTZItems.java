package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAuras;
import net.arkadiyhimself.fantazia.advanced.capacity.spellhandler.Spells;
import net.arkadiyhimself.fantazia.items.casters.DashStone;
import net.arkadiyhimself.fantazia.items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBlade;
import net.arkadiyhimself.fantazia.items.weapons.Melee.Murasama;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FTZItems extends FTZRegistry<Item> {
    private static final FTZItems INSTANCE = new FTZItems();
    private void magicItem(final String name, final Supplier<Item> sup) {
        RegistryObject<Item> item = this.register(name, sup);
        Fantazia.CommonRegistry.MAGIC_ITEM.add(item);
    }
    private void weaponItem(final String name, final Supplier<Item> sup) {
        RegistryObject<Item> item = this.register(name, sup);
        Fantazia.CommonRegistry.WEAPON_ITEM.add(item);
    }
    @ObjectHolder(value = Fantazia.MODID + ":fragile_blade", registryName = "item")
    public static final FragileBlade FRAGILE_BLADE = null;

    @ObjectHolder(value = Fantazia.MODID + ":murasama", registryName = "item")
    public static final Murasama MURASAMA = null;

    @ObjectHolder(value = Fantazia.MODID + ":wooden_hatchet", registryName = "item")
    public static final HatchetItem WOODEN_HATCHET = null;
    @ObjectHolder(value = Fantazia.MODID + ":stone_hatchet", registryName = "item")
    public static final HatchetItem STONE_HATCHET = null;
    @ObjectHolder(value = Fantazia.MODID + ":iron_hatchet", registryName = "item")
    public static final HatchetItem IRON_HATCHET = null;
    @ObjectHolder(value = Fantazia.MODID + ":golden_hatchet", registryName = "item")
    public static final HatchetItem GOLDEN_HATCHET = null;
    @ObjectHolder(value = Fantazia.MODID + ":diamond_hatchet", registryName = "item")
    public static final HatchetItem DIAMOND_HATCHET = null;
    @ObjectHolder(value = Fantazia.MODID + ":netherite_hatchet", registryName = "item")
    public static final HatchetItem NETHERITE_HATCHET = null;

    @ObjectHolder(value = Fantazia.MODID + ":dashstone1", registryName = "item")
    public static final DashStone DASHSTONE1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":dashstone2", registryName = "item")
    public static final DashStone DASHSTONE2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":dashstone3", registryName = "item")
    public static final DashStone DASHSTONE3 = null;

    @ObjectHolder(value = Fantazia.MODID + ":entangler", registryName = "item")
    public static final SpellCaster ENTANGLER = null;
    @ObjectHolder(value = Fantazia.MODID + ":soul_eater", registryName = "item")
    public static final SpellCaster SOUL_EATER = null;
    @ObjectHolder(value = Fantazia.MODID + ":heart_of_sculk", registryName = "item")
    public static final SpellCaster SCULK_HEART = null;
    @ObjectHolder(value = Fantazia.MODID + ":mystic_mirror", registryName = "item")
    public static final SpellCaster MYSTIC_MIRROR = null;
    @ObjectHolder(value = Fantazia.MODID + ":bloodlust_amulet", registryName = "item")
    public static final SpellCaster BLOODLUST_AMULET = null;
    @ObjectHolder(value = Fantazia.MODID + ":leaders_horn", registryName = "item")
    public static final SpellCaster LEADERS_HORN = null;
    @ObjectHolder(value = Fantazia.MODID + ":tranquil_herb", registryName = "item")
    public static final SpellCaster TRANQUIL_HERB = null;
    @ObjectHolder(value = Fantazia.MODID + ":spiral_nemesis", registryName = "item")
    public static final SpellCaster SPIRAL_NEMESIS = null;
    private FTZItems() {
        super(ForgeRegistries.ITEMS);

        weaponItem("fragile_blade", FragileBlade::new);
        weaponItem("murasama", Murasama::new);

        weaponItem("wooden_hatchet", () -> new HatchetItem(Tiers.WOOD, -2.6f, new Item.Properties()));
        weaponItem("stone_hatchet", () -> new HatchetItem(Tiers.STONE, -2.5f, new Item.Properties()));
        weaponItem("iron_hatchet", () -> new HatchetItem(Tiers.IRON, -2.4f, new Item.Properties()));
        weaponItem("golden_hatchet", () -> new HatchetItem(Tiers.GOLD, -1.5f, new Item.Properties()));
        weaponItem("diamond_hatchet", () -> new HatchetItem(Tiers.DIAMOND, -2.25f, new Item.Properties()));
        weaponItem("netherite_hatchet", () -> new HatchetItem(Tiers.NETHERITE, -2f, new Item.Properties()));

        magicItem("dashstone1", () -> new DashStone(1));
        magicItem("dashstone2", () -> new DashStone(2));
        magicItem("dashstone3", () -> new DashStone(3));


        magicItem("entangler", () -> new SpellCaster().setSpell(Spells.ENTANGLE));
        magicItem("soul_eater", () -> new SpellCaster().setSpell(Spells.DEVOUR));
        magicItem("heart_of_sculk", () -> new SpellCaster().setSpell(Spells.SONIC_BOOM));
        magicItem("mystic_mirror", () -> new SpellCaster().setSpell(Spells.REFLECT));
        magicItem("bloodlust_amulet", () -> new SpellCaster().setSpell(Spells.DAMNED_WRATH));
        magicItem("leaders_horn", () -> new SpellCaster().setAura(BasicAuras.LEADERSHIP));
        magicItem("tranquil_herb", () -> new SpellCaster().setAura(BasicAuras.TRANQUIL));
        magicItem("spiral_nemesis", () -> new SpellCaster().setAura(BasicAuras.DESPAIR));
    }

    @Override
    protected void onRegistry(RegisterEvent event) {
        FTZBlocks.getBlockItems().forEach((block, item) -> event.register(ForgeRegistries.Keys.ITEMS, block, () -> item.apply(ForgeRegistries.BLOCKS.getValue(block))));
    }
}
