package net.arkadiyhimself.fantazia.util.library.hierarchy;

import net.minecraft.resources.ResourceLocation;

public class HierarchyTransformers {
    public static IHierarchy<ResourceLocation> toResourceLocations(IHierarchy<String> hierarchy) {
        return hierarchy.transform(ResourceLocation::new);
    }
}
