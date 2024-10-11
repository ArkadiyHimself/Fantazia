package net.arkadiyhimself.fantazia.data.talent.types;

import net.arkadiyhimself.fantazia.data.talent.ITalentBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

public class CurioTalent extends BasicTalent {

    private final String ident;
    private final int amount;

    public CurioTalent(ResourceLocation iconTexture, String title, int wisdom, @Nullable ResourceLocation advancement, String ident, int amount) {
        super(iconTexture, title, wisdom, advancement);
        this.ident = ident;
        this.amount = amount;
    }

    @Override
    public void applyModifiers(@NotNull Player player) {
        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(player).orElse(null);
        if (curiosItemHandler == null) return;
        Optional<ICurioStacksHandler> optional = curiosItemHandler.getStacksHandler(ident);
        if (optional.isEmpty()) return;
        ICurioStacksHandler handler = optional.get();

        handler.addPermanentModifier(new AttributeModifier(getID(), amount, AttributeModifier.Operation.ADD_VALUE));
        handler.update();
    }

    @Override
    public void removeModifiers(@NotNull Player player) {
        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(player).orElse(null);
        if (curiosItemHandler == null) return;
        Optional<ICurioStacksHandler> optional = curiosItemHandler.getStacksHandler(ident);
        if (optional.isEmpty()) return;
        ICurioStacksHandler handler = optional.get();

        handler.removeModifier(getID());
        handler.update();
    }

    public static class Builder implements ITalentBuilder<CurioTalent> {

        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdom;
        private final ResourceLocation advancement;
        private final String ident;
        private final int amount;

        public Builder(ResourceLocation advancement, ResourceLocation iconTexture, String title, int wisdom, String ident, int amount) {
            this.advancement = advancement;
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdom = wisdom;
            this.ident = ident;
            this.amount = amount;
        }

        @Override
        public CurioTalent build() {
            return new CurioTalent(iconTexture, title, wisdom, advancement, ident, amount);
        }
    }
}
