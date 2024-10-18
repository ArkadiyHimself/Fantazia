package net.arkadiyhimself.fantazia.data.talent.types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public record CurioTalent(BasicProperties properties, String ident, int amount) implements ITalent {

    @Override
    public BasicProperties getProperties() {
        return properties;
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

    @Override
    public String toString() {
        return "CurioTalent{" + getID() + "}";
    }

    public static class Builder extends ITalentBuilder.AbstractBuilder<CurioTalent> {

        private final String ident;
        private final int amount;

        public Builder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement, String ident, int amount) {
            super(iconTexture, title, wisdom, advancement);

            this.ident = ident;
            this.amount = amount;
        }

        @Override
        public CurioTalent build(ResourceLocation identifier) {
            return new CurioTalent(buildProperties(identifier), ident, amount);
        }
    }
}
