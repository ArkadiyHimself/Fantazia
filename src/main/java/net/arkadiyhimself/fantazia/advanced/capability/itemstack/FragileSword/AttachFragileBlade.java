package net.arkadiyhimself.fantazia.advanced.capability.itemstack.FragileSword;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.Items.weapons.Melee.FragileBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class AttachFragileBlade extends CapabilityAttacher {
    private static final Class<FragileBladeCap> FRAGILE_BLADE_CLASS = FragileBladeCap.class;
    public static final Capability<FragileBladeCap> FRAGILE_BLADE_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation FRAGILE_BLADE_CAPABILITY_RL = Fantazia.res("itemstack_fragile_blade");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static FragileBladeCap getUnwrap(ItemStack itemStack) {
        return get(itemStack).orElse(null);
    }

    public static LazyOptional<FragileBladeCap> get(ItemStack itemStack) {
        return itemStack.getCapability(FRAGILE_BLADE_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        if (itemStack.getItem() instanceof FragileBlade) {
            genericAttachCapability(event, new FragileBladeCap(itemStack), FRAGILE_BLADE_CAPABILITY, FRAGILE_BLADE_CAPABILITY_RL);
        }
    }

    public static void register() {
        CapabilityAttacher.registerCapability(FRAGILE_BLADE_CLASS);
        CapabilityAttacher.registerItemStackAttacher(AttachFragileBlade::attach, AttachFragileBlade::get);
    }
}
