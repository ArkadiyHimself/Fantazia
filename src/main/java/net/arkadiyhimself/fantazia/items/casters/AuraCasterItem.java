package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuraCasterItem extends Item {

    private final Holder<Aura> basicAura;

    public AuraCasterItem(Holder<Aura> basicAura) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.basicAura = basicAura;
    }

    public Holder<Aura> getAura() {
        return basicAura;
    }

    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        if (!Screen.hasShiftDown()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
            String basicPath = "item." + id.getNamespace() + "." + id.getPath();
            int lines = 0;
            String desc = Component.translatable(basicPath + ".lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.literal(" "));
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, null, null));
            }
        }
        else components.addAll(basicAura.value().buildTooltip());

        return components;
    }
}
