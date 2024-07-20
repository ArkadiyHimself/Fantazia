package net.arkadiyhimself.fantazia.util.Interfaces;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IChangingIcon {
    @OnlyIn(Dist.CLIENT)
    void registerVariants();
}
