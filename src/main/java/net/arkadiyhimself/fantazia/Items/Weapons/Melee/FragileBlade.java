package net.arkadiyhimself.fantazia.Items.Weapons.Melee;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.arkadiyhimself.fantazia.util.Interfaces.IChangingIcon;
import net.arkadiyhimself.fantazia.util.Interfaces.ITooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class FragileBlade extends MeleeWeaponItem implements IChangingIcon, ITooltipBuilder {
    public FragileBlade() {
        super(new Item.Properties().stacksTo(1).defaultDurability(1024),-1.5f, 4, "fragile_blade");
    }
    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        return super.getShareTag(stack);
       // return AttachFragileBlade.getUnwrap(stack).serializeNBT(true);
    }
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt != null) {
            AttachFragileBlade.get(stack).ifPresent(fragileBladeCap -> fragileBladeCap.deserializeNBT(nbt, true));
        }
        super.readShareTag(stack,nbt);
    }
    @Override
    public void registerVariants() {
        ItemProperties.register(this, Fantazia.res("dmg"),
                ((pStack, pLevel, pEntity, pSeed) -> {
                    FragileBladeCap cap = AttachFragileBlade.getUnwrap(pStack);
                    if (cap == null) {
                        return 0f;
                    } else {
                        return cap.level;
                    }
                }));
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return Fantazia.getItemsRenderer();
            }
        });
    }
    @Override
    public List<Component> buildTooltip(@Nullable ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        FragileBladeCap cap = AttachFragileBlade.getUnwrap(stack);
        if (cap == null) return components;
        String basicPath = "weapon.fantazia.hidden_potential";
        int lines;

        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {
                return components;
            }

            ChatFormatting[] noshift = new ChatFormatting[]{ChatFormatting.RED};
            for (int i = 1; i <= lines; i++) {
                WhereMagicHappens.Gui.addComponent(components, basicPath + ".desc." + i, noshift, null);
            }
            components.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(components, basicPath + ".current_damage", noshift, cap.getDamageFormatting(), cap.damage);
            return components;
        }

        WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.common.weapon", new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, Component.translatable("weapon.fantazia.hidden_potential.name").getString());
        components.add(Component.translatable(" "));
        String text = Component.translatable(basicPath + ".lines").getString();

        try {
            lines = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return components;
        }

        ChatFormatting[] main = new ChatFormatting[]{ChatFormatting.GOLD};
        for (int i = 1; i <= lines; i++) {
            WhereMagicHappens.Gui.addComponent(components, basicPath + "." + i, main, null);
        }

        components.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(components, basicPath + ".minimal_damage", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}, cap.minDMG());
        WhereMagicHappens.Gui.addComponent(components, basicPath + ".maximum_damage", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}, cap.maxDMG());

        return components;
    }
}
