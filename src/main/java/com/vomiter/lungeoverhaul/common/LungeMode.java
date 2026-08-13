package com.vomiter.lungeoverhaul.common;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Optional;

public enum LungeMode {
    PLAYER_DEFAULT("player_default", false, false),
    DISABLED("disabled", false, false),
    JAB_ONLY("jab_only", true, false),
    CHARGE_ONLY("charge_only", false, true),
    BOTH("both", true, true);

    public enum Arg{
        DISABLED(),
        JAB_ONLY(),
        CHARGE_ONLY(),
        BOTH();
    }

    private final String id;
    private final boolean jab;
    private final boolean charge;

    LungeMode(String id, boolean jab, boolean charge) {
        this.id = id;
        this.jab = jab;
        this.charge = charge;
    }

    public String id() { return id; }
    public Component displayName() {
        return Component.translatable("lunge_mode.lungeoverhaul." + id());
    }
    public boolean enablesJab() { return jab; }
    public boolean enablesCharge() { return charge; }

    public LungeMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static Optional<LungeMode> byId(String id) {
        return Arrays.stream(values()).filter(mode -> mode.id.equals(id)).findFirst();
    }
}
