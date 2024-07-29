package net.arkadiyhimself.fantazia.util.interfaces;

import net.arkadiyhimself.fantazia.events.custom.VanillaEventsExtension;

public interface IHealReacting {
    void onHeal(VanillaEventsExtension.AdvancedHealEvent event);
}
