package net.arkadiyhimself.fantazia.util.Interfaces;

import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.AbilityProviding.Talent;

public interface ITalentRequire {
    Talent required();
    void onTalentUnlock(Talent talent);
}
