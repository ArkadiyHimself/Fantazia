package net.arkadiyhimself.fantazia.api.capability.itemstack;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class StackDataGetter extends CapabilityAttacher {
    private static final Class<StackDataManager> STACK_DATA_CASS = StackDataManager.class;
    public static final Capability<StackDataManager> STACK_DATA = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation STACK_DATA_RL = Fantazia.res("stack_data");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static StackDataManager getUnwrap(ItemStack itemStack) {
        return get(itemStack).orElse(null);
    }

    public static LazyOptional<StackDataManager> get(ItemStack itemStack) {
        return itemStack.getCapability(STACK_DATA);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        genericAttachCapability(event, new StackDataManager(itemStack), STACK_DATA, STACK_DATA_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(STACK_DATA_CASS);
        CapabilityAttacher.registerItemStackAttacher(StackDataGetter::attach, StackDataGetter::get);
    }
}
