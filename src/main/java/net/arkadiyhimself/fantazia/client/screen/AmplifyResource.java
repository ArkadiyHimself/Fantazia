package net.arkadiyhimself.fantazia.client.screen;

public enum AmplifyResource {

    REGULAR,
    NOT_ENOUGH,
    ENOUGH;

    public boolean isEnough() {
        return this == ENOUGH;
    }

    public boolean notEnough() {
        return this == NOT_ENOUGH;
    }
}
