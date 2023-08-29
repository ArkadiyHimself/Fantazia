package net.arkadiyhimself.combatimprovement.Registries.Items;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.DashStone;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.LifeDeathEntangler;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.SoulEater;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.FragileBlade;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Murasama;
import net.arkadiyhimself.combatimprovement.Registries.Sounds.SoundRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CombatImprovement.MODID);
    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }
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
    static {
        FRAGILE_BLADE = regItem("fragile_blade", FragileBlade::new);

        DASHSTONE1 = regItem("dashstone1", () -> new DashStone(1));
        DASHSTONE2 = regItem("dashstone2", () -> new DashStone(2));
        DASHSTONE3 = regItem("dashstone3", () -> new DashStone(3));

        MURASAMA = regItem("murasama", Murasama::new);
        
        ENTANGLER = regItem("entangler", () -> new LifeDeathEntangler(SoundRegistry.ENTANGLE.get()));

        SOUL_EATER = regItem("soul_eater", () -> new SoulEater(SoundRegistry.DEVOUR.get()));
    }
}
