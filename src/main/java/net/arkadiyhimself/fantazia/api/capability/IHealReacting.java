package net.arkadiyhimself.fantazia.api.capability;

import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;

public interface IHealReacting {
    void onHeal(VanillaEventsExtension.AdvancedHealEvent event);
}
