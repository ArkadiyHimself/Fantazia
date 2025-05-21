package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.advanced.runes.Rune;
import net.arkadiyhimself.fantazia.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotPredicate;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.util.EquipCurioTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FantazicUtil {

    public static void playSoundUI(Player player, SoundEvent soundEvent, float pitch, float volume) {
        if (player == Minecraft.getInstance().player) playSoundUI(soundEvent, pitch, volume);
    }

    public static void playSoundUI(SoundEvent soundEvent, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume));
    }

    public static void playSoundUI(SoundEvent soundEvent) {
        playSoundUI(soundEvent,1f,1f);
    }

    public static List<ItemStack> fullInventory(Player player) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.addAll(player.getInventory().items);
        itemStacks.addAll(player.getInventory().offhand);
        itemStacks.addAll(player.getInventory().armor);
        return itemStacks;
    }

    public static boolean hasCurio(final LivingEntity entity, final Item curio) {
        AtomicBoolean present = new AtomicBoolean(false);
        CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
            List<SlotResult> slots = inventory.findCurios(curio);
            if (!slots.isEmpty()) present.set(true);
        });
        return present.get();
    }

    public static int duplicatingCurio(final LivingEntity entity, final Item item) {
        AtomicInteger present = new AtomicInteger(0);
        CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
            List<SlotResult> slots = inventory.findCurios(item);
            present.set(slots.size());
        });
        return present.get();
    }

    public static List<SlotResult> findAllCurios(LivingEntity entity, String ident) {
        List<SlotResult> result = new ArrayList<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return result;
        Map<String, ICurioStacksHandler> curios = handler.getCurios();
        for (String id : curios.keySet()) {
            if (id.contains(ident)) {
                ICurioStacksHandler stacksHandler = curios.get(id);
                IDynamicStackHandler stackHandler = stacksHandler.getStacks();

                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);

                    NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                    result.add(new SlotResult(new SlotContext(id, handler.getWearer(), i, false,
                            renderStates.size() > i && renderStates.get(i)), stack));
                }
            }
        }
        return result;
    }

    public static Optional<SlotResult> findCurio(LivingEntity entity, String ident, int id) {
        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curiosItemHandler == null) return Optional.empty();
        return curiosItemHandler.findCurio(ident, id);
    }

    public static List<Holder<Rune>> findRunes(LivingEntity entity) {
        List<SlotResult> slotResults = findAllCurios(entity, FTZSlots.RUNE);
        if (slotResults.isEmpty()) return Lists.newArrayList();
        List<Holder<Rune>> runes = Lists.newArrayList();
        for (SlotResult slotResult : slotResults) {
            Holder<Rune> holder = slotResult.stack().get(FTZDataComponentTypes.RUNE);
            if (holder != null) runes.add(holder);
        }
        return runes;
    }

    public static boolean hasRune(LivingEntity entity, Holder<Rune> rune) {
        return findRunes(entity).contains(rune);
    }

    public static ItemPredicate itemTagPredicate(TagKey<Item> tagKey) {
        return ItemPredicate.Builder.item().of(tagKey).build();
    }

    public static <T> Criterion<EquipCurioTrigger.TriggerInstance> equipCurioTrigger(DataComponentType<T> component, T expected, String slot) {
        ItemPredicate itemPredicate = ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(component, expected).build()).build();
        SlotPredicate slotPredicate = SlotPredicate.Builder.slot().of(slot).build();

        return EquipCurioTrigger.INSTANCE.createCriterion(new EquipCurioTrigger.TriggerInstance(Optional.empty(), Optional.of(itemPredicate), Optional.empty(), Optional.of(slotPredicate)));
    }

    public static Criterion<EquipCurioTrigger.TriggerInstance> equipCurioTrigger(Item item, String slot) {
        ItemPredicate itemPredicate = ItemPredicate.Builder.item().of(item).build();
        SlotPredicate slotPredicate = SlotPredicate.Builder.slot().of(slot).build();

        return EquipCurioTrigger.INSTANCE.createCriterion(new EquipCurioTrigger.TriggerInstance(Optional.empty(), Optional.of(itemPredicate), Optional.empty(), Optional.of(slotPredicate)));
    }

    public static boolean isActiveCaster(Item item) {
        return item instanceof SpellCasterItem spellCaster && spellCaster.getSpell().value().isActive();
    }

    public static boolean isPassiveCaster(Item item) {
        return item instanceof SpellCasterItem spellCaster && !spellCaster.getSpell().value().isActive() || item instanceof AuraCasterItem;
    }

    public static ItemStack dashStone(int level) {
        ItemStack itemStack = new ItemStack(FTZItems.DASHSTONE.value());
        itemStack.update(FTZDataComponentTypes.DASH_LEVEL, 1, integer -> level);
        return itemStack;
    }

    public static ItemPredicate dashStonePredicate(int level) {
        return ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(FTZDataComponentTypes.DASH_LEVEL.value(), level).build()).build();
    }
}
