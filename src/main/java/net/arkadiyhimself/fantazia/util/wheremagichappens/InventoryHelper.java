package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryHelper {

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

}
