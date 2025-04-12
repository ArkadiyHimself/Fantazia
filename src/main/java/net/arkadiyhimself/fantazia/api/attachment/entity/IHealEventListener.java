package net.arkadiyhimself.fantazia.api.attachment.entity;

import net.arkadiyhimself.fantazia.api.custom_events.VanillaEventsExtension;

public interface IHealEventListener {
    void onHeal(VanillaEventsExtension.AdvancedHealEvent event);
}
