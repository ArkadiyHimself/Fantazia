package net.arkadiyhimself.fantazia.api.prompt;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public record PromptToast(Prompt prompt) implements Toast {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");

    @Override
    public @NotNull Object getToken() {
        return prompt;
    }

    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastComponent, long delta) {
        guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
        Font font = toastComponent.getMinecraft().font;

        String title = prompt.title();
        List<Supplier<Object>> forTitle = prompt().forTitle();
        Object[] titleObjects = new Object[forTitle.size()];
        for (int i = 0; i < forTitle.size(); i++) titleObjects[i] = forTitle.get(i).get();
        MutableComponent titleComponent = Component.translatable(title, titleObjects);

        String text = prompt.text();
        List<Supplier<Object>> forText = prompt().forText();
        Object[] textObjects = new Object[forText.size()];
        for (int i = 0; i < forText.size(); i++) textObjects[i] = forText.get(i).get();
        MutableComponent textComponent = Component.translatable(text, textObjects);

        guiGraphics.drawString(font, titleComponent, 30, 7, 0xffffffff);
        guiGraphics.drawString(font, textComponent, 30, 17, 0xffffffff);

        guiGraphics.blitSprite(prompt.sprite(), 6, 6, 20, 20);
        return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
