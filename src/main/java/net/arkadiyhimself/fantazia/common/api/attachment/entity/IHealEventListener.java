package net.arkadiyhimself.fantazia.common.api.attachment.entity;

import net.arkadiyhimself.fantazia.common.api.custom_events.VanillaEventsExtension;

public interface IHealEventListener {
    void onHeal(VanillaEventsExtension.AdvancedHealEvent event);
}
