package net.arkadiyhimself.fantazia.data.talent;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record TalentImpact(ResourceLocation id, Consumer<Player> apply, Consumer<Player> remove, Consumer<Player> disable, Consumer<Player> enable) {

    public static final Codec<TalentImpact> CODEC = ResourceLocation.CODEC.xmap(TalentImpacts::getImpact, TalentImpact::id);

    public void apply(Player player) {
        apply.accept(player);
    }

    public void remove(Player player) {
        remove.accept(player);
    }

    public void disable(Player player) {
        disable.accept(player);
    }

    public void enable(Player player) {
        enable.accept(player);
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static class Builder {

        private final ResourceLocation id;
        private Consumer<Player> apply = player -> {};
        private Consumer<Player> remove = player -> {};
        private Consumer<Player> disable = player -> {};
        private Consumer<Player> enable = player -> {};

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder apply(Consumer<Player> apply) {
            this.apply = apply;
            return this;
        }

        public Builder remove(Consumer<Player> remove) {
            this.remove = remove;
            return this;
        }

        public Builder disable(Consumer<Player> apply) {
            this.disable = apply;
            return this;
        }

        public Builder enable(Consumer<Player> remove) {
            this.enable = remove;
            return this;
        }

        public Builder defaultDisabling() {
            this.disable = remove;
            this.enable = apply;
            return this;
        }

        public TalentImpact build() {
            return new TalentImpact(id, apply, remove, disable, enable);
        }
    }
}
