package net.arkadiyhimself.combatimprovement.api;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Items.MagicCasters.DashStone;
import net.arkadiyhimself.combatimprovement.Items.MagicCasters.ActiveAndTargeted.LifeDeathEntangler;
import net.arkadiyhimself.combatimprovement.Items.MagicCasters.ActiveAndTargeted.SculkHeart;
import net.arkadiyhimself.combatimprovement.Items.MagicCasters.ActiveAndTargeted.SoulEater;
import net.arkadiyhimself.combatimprovement.Items.MagicCasters.Passive.PassiveCasters;
import net.arkadiyhimself.combatimprovement.Items.Weapons.Melee.FragileBlade;
import net.arkadiyhimself.combatimprovement.Items.Weapons.Melee.Murasama;
import net.arkadiyhimself.combatimprovement.Items.Weapons.Mixed.Hatchet;
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
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CombatImprovement.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CombatImprovement.MODID);
    public static final RegistryObject<CreativeModeTab> COMBAT_TAB = CREATIVE_MODE_TAB.register("combat_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.FRAGILE_BLADE.get()))
                    .title(Component.translatable("Combat Improvement").withStyle(ChatFormatting.DARK_PURPLE)).build());
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TAB.register(eventBus);
    }

    public static RegistryObject<Item> regItem(final String name, final Supplier<? extends Item> sup) {
        RegistryObject<Item> item = ITEMS.register(name, sup);
        CombatImprovement.commonRegistry.ModItems.add(item);
        return item;
    }
    public static final RegistryObject<Item> FRAGILE_BLADE;
    public static final RegistryObject<Item> DASHSTONE1;
    public static final RegistryObject<Item> DASHSTONE2;
    public static final RegistryObject<Item> DASHSTONE3;
    public static final RegistryObject<Item> MURASAMA;
    public static final RegistryObject<Item> ENTANGLER;
    public static final RegistryObject<Item> SOUL_EATER;
    public static final RegistryObject<Item> SCULK_HEART;
    public static final RegistryObject<Item> MYSTIC_MIRROR;
    public static final RegistryObject<Item> WOODEN_HATCHET;
    public static final RegistryObject<Item> STONE_HATCHET;
    public static final RegistryObject<Item> IRON_HATCHET;
    public static final RegistryObject<Item> GOLDEN_HATCHET;
    public static final RegistryObject<Item> DIAMOND_HATCHET;
    public static final RegistryObject<Item> NETHERITE_HATCHET;

    static {
        FRAGILE_BLADE = regItem("fragile_blade", FragileBlade::new);

        DASHSTONE1 = regItem("dashstone1", () -> new DashStone(1));
        DASHSTONE2 = regItem("dashstone2", () -> new DashStone(2));
        DASHSTONE3 = regItem("dashstone3", () -> new DashStone(3));

        MURASAMA = regItem("murasama", Murasama::new);
        
        ENTANGLER = regItem("entangler", () -> new LifeDeathEntangler(SoundRegistry.ENTANGLE.get()));

        SOUL_EATER = regItem("soul_eater", () -> new SoulEater(SoundRegistry.DEVOUR.get()));

        SCULK_HEART = regItem("heart_of_sculk", SculkHeart::new);

        MYSTIC_MIRROR = regItem("mystic_mirror", () -> new PassiveCasters(Component.translatable("tooltip.combatimprovement.mystic_mirror.ability")
                .withStyle(), SoundRegistry.MYSTIC_MIRROR.get(), 1.5f, 200));

        WOODEN_HATCHET = regItem("wooden_hatchet", () -> new Hatchet(Tiers.WOOD, 1, -2f, new Item.Properties(),"wooden_hatchet"));
        STONE_HATCHET = regItem("stone_hatchet", () -> new Hatchet(Tiers.STONE, 1, -2f, new Item.Properties(),"stone_hatchet"));
        IRON_HATCHET = regItem("iron_hatchet", () -> new Hatchet(Tiers.IRON, 1, -2f, new Item.Properties(),"iron_hatchet"));
        GOLDEN_HATCHET = regItem("golden_hatchet", () -> new Hatchet(Tiers.GOLD, 1, -1.5f, new Item.Properties(),"golden_hatchet"));
        DIAMOND_HATCHET = regItem("diamond_hatchet", () -> new Hatchet(Tiers.DIAMOND, 1, -1.7f, new Item.Properties(),"diamond_hatchet"));
        NETHERITE_HATCHET = regItem("netherite_hatchet", () -> new Hatchet(Tiers.NETHERITE, 1, -1.6f, new Item.Properties(),"netherite_hatchet"));
    }
}
