package net.arkadiyhimself.fantazia.api.items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IChangingIcon {
    @OnlyIn(Dist.CLIENT)
    void registerVariants();
}
