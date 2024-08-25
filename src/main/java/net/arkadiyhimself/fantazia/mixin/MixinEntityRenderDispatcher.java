package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.render.layers.AbsoluteBarrier;
import net.arkadiyhimself.fantazia.client.render.layers.BarrierLayer;
import net.arkadiyhimself.fantazia.client.render.layers.LayeredBarrierLayer;
import net.arkadiyhimself.fantazia.client.render.layers.MysticMirror;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    @Shadow public Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Shadow private Map<String, EntityRenderer<? extends Player>> playerRenderers;

    @SuppressWarnings("unchecked")
    @Inject(at = @At(value = "TAIL"), method = "onResourceManagerReload")
    private <T extends LivingEntity, M extends EntityModel<T>> void addLayer(ResourceManager pResourceManager, CallbackInfo ci) {
        List<LivingEntityRenderer<?,?>> livingEntityRenderers = Lists.newArrayList();
        for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : renderers.entrySet()) if (entry.getValue() instanceof LivingEntityRenderer<?,?> livingEntityRenderer) livingEntityRenderers.add(livingEntityRenderer);
        for (Map.Entry<String, EntityRenderer<? extends Player>> entry : playerRenderers.entrySet()) if (entry.getValue() instanceof PlayerRenderer playerRenderer) livingEntityRenderers.add(playerRenderer);
        Fantazia.LOGGER.info("LivingEntityRenderers: " + livingEntityRenderers.size());
        for (LivingEntityRenderer<?,?> livingEntityRenderer : livingEntityRenderers) {
            Fantazia.LOGGER.info("Adding layers...");
            LivingEntityRenderer<T,M> renderer = (LivingEntityRenderer<T, M>) livingEntityRenderer;
            Fantazia.LOGGER.info("Renderer castes...");
            renderer.addLayer(new BarrierLayer.LayerBarrier<>(renderer));
            renderer.addLayer(new LayeredBarrierLayer.LayerBarrier<>(renderer));
            renderer.addLayer(new AbsoluteBarrier.LayerBarrier<>(renderer));
            renderer.addLayer(new MysticMirror.LayerMirror<>(renderer));
            Fantazia.LOGGER.info("Layers are added!");
        }
    }
}
