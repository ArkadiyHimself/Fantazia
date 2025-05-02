package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

public class DurationHolder {

    private int initialDur = 1;
    private int dur = 0;

    public void setInitialDur(int initialDur) {
        // avoiding zero division in some cases
        this.initialDur = Math.max(1, initialDur);
    }

    public void setDur(int dur) {
        this.dur = dur;
    }

    public int initialDur() {
        return initialDur;
    }

    public int dur() {
        return dur;
    }

    public float percent() {
        return (float) dur / initialDur;
    }

    public boolean present() {
        return dur == -1 || dur > 0;
    }
}
