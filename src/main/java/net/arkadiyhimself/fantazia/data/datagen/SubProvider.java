package net.arkadiyhimself.fantazia.data.datagen;

import net.minecraft.core.HolderLookup;

import java.util.function.Consumer;

public interface SubProvider<T> {

    void generate(HolderLookup.Provider provider, Consumer<T> consumer);
}
