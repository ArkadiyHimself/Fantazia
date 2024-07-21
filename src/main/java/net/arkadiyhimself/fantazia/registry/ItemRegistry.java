package net.arkadiyhimself.fantazia.registry;

import net.arkadiyhimself.fantazia.advanced.capacity.SpellHandler.Spells;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAuras;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.Items.casters.DashStone;
import net.arkadiyhimself.fantazia.Items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.Items.weapons.Melee.FragileBlade;
import net.arkadiyhimself.fantazia.Items.weapons.Melee.Murasama;
import net.arkadiyhimself.fantazia.Items.weapons.Range.HatchetItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Fantazia.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Fantazia.MODID);
    public static final RegistryObject<CreativeModeTab> MAGIC_TAB = CREATIVE_MODE_TAB.register("magic_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.ENTANGLER.get()))
                    .title(Component.translatable("fantazia.creativetab.magic").withStyle(ChatFormatting.DARK_PURPLE)).build());
    public static final RegistryObject<CreativeModeTab> WEAPON_TAB = CREATIVE_MODE_TAB.register("weapon_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.FRAGILE_BLADE.get()))
                    .title(Component.translatable("fantazia.creativetab.weapon").withStyle(ChatFormatting.DARK_PURPLE)).build());
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TAB.register(eventBus);
    }

    public static RegistryObject<Item> regMagicItem(final String name, final Supplier<? extends Item> sup) {
        RegistryObject<Item> item = ITEMS.register(name, sup);
        Fantazia.commonRegistry.MagicItems.add(item);
        return item;
    }
    public static RegistryObject<Item> regWeaponItem(final String name, final Supplier<? extends Item> sup) {
        RegistryObject<Item> item = ITEMS.register(name, sup);
        Fantazia.commonRegistry.WeaponItems.add(item);
        return item;
    }
    public static final RegistryObject<Item> FRAGILE_BLADE; // finished
    public static final RegistryObject<Item> DASHSTONE1; // finished
    public static final RegistryObject<Item> DASHSTONE2; // finished
    public static final RegistryObject<Item> DASHSTONE3; // finished
    public static final RegistryObject<Item> MURASAMA; // finished
    public static final RegistryObject<Item> ENTANGLER; // finished
    public static final RegistryObject<Item> SOUL_EATER; // finished
    public static final RegistryObject<Item> SCULK_HEART; // finished
    public static final RegistryObject<Item> MYSTIC_MIRROR; // finished
    public static final RegistryObject<Item> BLOODLUST_AMULET;

    public static final RegistryObject<Item> LEADERS_HORN; // finished
    public static final RegistryObject<Item> TRANQUIL_HERB; // finished
    public static final RegistryObject<Item> SPIRAL_NEMESIS; // finished

    public static final RegistryObject<Item> WOODEN_HATCHET; // finished
    public static final RegistryObject<Item> STONE_HATCHET; // finished
    public static final RegistryObject<Item> IRON_HATCHET; // finished
    public static final RegistryObject<Item> GOLDEN_HATCHET; // finished
    public static final RegistryObject<Item> DIAMOND_HATCHET; // finished
    public static final RegistryObject<Item> NETHERITE_HATCHET; // finished


    static {
        FRAGILE_BLADE = regWeaponItem("fragile_blade", FragileBlade::new);

        DASHSTONE1 = regMagicItem("dashstone1", () -> new DashStone(1));
        DASHSTONE2 = regMagicItem("dashstone2", () -> new DashStone(2));
        DASHSTONE3 = regMagicItem("dashstone3", () -> new DashStone(3));

        MURASAMA = regWeaponItem("murasama", Murasama::new);
        
        ENTANGLER = regMagicItem("entangler", () -> new SpellCaster().setSpell(Spells.ENTANGLE));

        SOUL_EATER = regMagicItem("soul_eater", () -> new SpellCaster().setSpell(Spells.DEVOUR));

        SCULK_HEART = regMagicItem("heart_of_sculk", () -> new SpellCaster().setSpell(Spells.SONIC_BOOM));

        MYSTIC_MIRROR = regMagicItem("mystic_mirror", () -> new SpellCaster().setSpell(Spells.REFLECT));

        BLOODLUST_AMULET = regMagicItem("bloodlust_amulet", () -> new SpellCaster().setSpell(Spells.DAMNED_WRATH));

        LEADERS_HORN = regMagicItem("leaders_horn", () -> new SpellCaster().setAura(BasicAuras.LEADERSHIP));

        TRANQUIL_HERB = regMagicItem("tranquil_herb", () -> new SpellCaster().setAura(BasicAuras.TRANQUIL));

        SPIRAL_NEMESIS = regMagicItem("spiral_nemesis", () -> new SpellCaster().setAura(BasicAuras.DESPAIR));

        WOODEN_HATCHET = regWeaponItem("wooden_hatchet", () -> new HatchetItem(Tiers.WOOD,2.5f,-2.6f, new Item.Properties(),"wooden_hatchet"));
        STONE_HATCHET = regWeaponItem("stone_hatchet", () -> new HatchetItem(Tiers.STONE,2.5f,-2.5f, new Item.Properties(),"stone_hatchet"));
        IRON_HATCHET = regWeaponItem("iron_hatchet", () -> new HatchetItem(Tiers.IRON,2.5f,-2.4f, new Item.Properties(),"iron_hatchet"));
        GOLDEN_HATCHET = regWeaponItem("golden_hatchet", () -> new HatchetItem(Tiers.GOLD,2.5f,-1.5f, new Item.Properties(),"golden_hatchet"));
        DIAMOND_HATCHET = regWeaponItem("diamond_hatchet", () -> new HatchetItem(Tiers.DIAMOND,2.5f,-2.25f, new Item.Properties(),"diamond_hatchet"));
        NETHERITE_HATCHET = regWeaponItem("netherite_hatchet", () -> new HatchetItem(Tiers.NETHERITE,2.5f,-2f, new Item.Properties(),"netherite_hatchet"));
    }
}
