package net.arkadiyhimself.fantazia.api.type.entity;

import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;

public interface IHealListener {
    void onHeal(VanillaEventsExtension.AdvancedHealEvent event);
}
