package net.arkadiyhimself.fantazia.items.weapons.Melee;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataManager;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.stackdata.HiddenPotential;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.util.interfaces.IChangingIcon;
import net.arkadiyhimself.fantazia.util.interfaces.ITooltipBuilder;
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
        CompoundTag tag = new CompoundTag();
        StackDataGetter.get(stack).ifPresent(stackDataManager -> tag.merge(stackDataManager.serializeNBT(true)));
        return tag;
    }
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt == null) return;
        StackDataGetter.get(stack).ifPresent(stackDataManager -> stackDataManager.deserializeNBT(nbt, true));
        super.readShareTag(stack,nbt);
    }
    @Override
    public void registerVariants() {
        ItemProperties.register(this, Fantazia.res("dmg"),
                ((pStack, pLevel, pEntity, pSeed) -> {
                    StackDataManager stackDataManager = StackDataGetter.getUnwrap(pStack);
                    if (stackDataManager == null) return 0f;
                    HiddenPotential hiddenPotential = stackDataManager.takeData(HiddenPotential.class);
                    if (hiddenPotential == null) return 0f;
                    else return hiddenPotential.damageLevel().getLevel();
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
        StackDataManager stackDataManager = StackDataGetter.getUnwrap(stack);
        if (stackDataManager == null) return components;
        HiddenPotential hiddenPotential = stackDataManager.takeData(HiddenPotential.class);
        if (hiddenPotential == null) return components;
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
            for (int i = 1; i <= lines; i++) GuiHelper.addComponent(components, basicPath + ".desc." + i, noshift, null);

            components.add(Component.translatable(" "));
            GuiHelper.addComponent(components, basicPath + ".current_damage", noshift, hiddenPotential.getFormatting(), hiddenPotential.getDamage() + this.getDamage() + 1);
            return components;
        }

        GuiHelper.addComponent(components, "tooltip.fantazia.common.weapon", new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, Component.translatable("weapon.fantazia.hidden_potential.name").getString());
        components.add(Component.translatable(" "));
        String text = Component.translatable(basicPath + ".lines").getString();

        try {
            lines = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return components;
        }

        ChatFormatting[] main = new ChatFormatting[]{ChatFormatting.GOLD};
        for (int i = 1; i <= lines; i++) {
            GuiHelper.addComponent(components, basicPath + "." + i, main, null);
        }

        components.add(Component.translatable(" "));
        GuiHelper.addComponent(components, basicPath + ".minimal_damage", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}, hiddenPotential.minDMG() + this.getDamage());
        GuiHelper.addComponent(components, basicPath + ".maximum_damage", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}, hiddenPotential.maxDMG() + this.getDamage());

        return components;
    }
}
