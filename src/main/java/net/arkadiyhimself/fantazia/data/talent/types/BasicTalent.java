package net.arkadiyhimself.fantazia.data.talent.types;

import net.minecraft.resources.ResourceLocation;

public record BasicTalent(ITalent.BasicProperties properties) implements ITalent {

    @Override
    public BasicProperties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "BasicTalent{" + getID() + "}";
    }

    public static final class Builder extends ITalentBuilder.AbstractBuilder<BasicTalent> {

        public Builder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement) {
            super(iconTexture, title, wisdom, advancement);
        }

        @Override
        public BasicTalent build(ResourceLocation identifier) {
            return new BasicTalent(buildProperties(identifier));
        }
    }
}

