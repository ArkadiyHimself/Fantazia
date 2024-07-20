package net.arkadiyhimself.fantazia.util.Capability.ItemStack.Common;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class AttachCommonItem extends CapabilityAttacher {
    private static final Class<CommonItem> COMMON_ITEM_CLASS = CommonItem.class;
    public static final Capability<CommonItem> COMMON_ITEM_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation COMMON_ITEM_CAPABILITY_RL = Fantazia.res("itemstack_common");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static CommonItem getUnwrap(ItemStack itemStack) {
        return get(itemStack).orElse(null);
    }

    public static LazyOptional<CommonItem> get(ItemStack itemStack) {
        return itemStack.getCapability(COMMON_ITEM_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        genericAttachCapability(event, new CommonItem(itemStack), COMMON_ITEM_CAPABILITY, COMMON_ITEM_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(COMMON_ITEM_CLASS);
        CapabilityAttacher.registerItemStackAttacher(AttachCommonItem::attach, AttachCommonItem::get);
    }
}
