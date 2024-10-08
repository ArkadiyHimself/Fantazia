package net.arkadiyhimself.fantazia.api.type.item;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IChangingIcon {
    @OnlyIn(Dist.CLIENT)
    void registerVariants();
}
