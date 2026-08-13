package com.vomiter.lungeoverhaul;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LungeConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue INITIAL_SPEED;
    public static final ModConfigSpec.DoubleValue MAX_HORIZONTAL_SPEED;
    public static final ModConfigSpec.DoubleValue TICK_ACCELERATION;
    public static final ModConfigSpec.DoubleValue ACTIVATION_EXHAUSTION;
    public static final ModConfigSpec.DoubleValue TICK_EXHAUSTION;
    public static final ModConfigSpec.DoubleValue HIT_EXHAUSTION;
    public static final ModConfigSpec.IntValue MIN_FOOD_LEVEL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("charge");

        builder.comment("Forward speed added immediately when a Charge begins.");
        INITIAL_SPEED = builder.defineInRange("initialSpeed", 0.55D, 0.0D, 4.0D);

        builder.comment("Charge stops adding acceleration at this horizontal forward speed; it does not cap the player's actual speed.");
        MAX_HORIZONTAL_SPEED = builder.defineInRange("maxHorizontalSpeed", 1.25D, 0.0D, 8.0D);

        builder.comment("Forward speed added each tick until maxHorizontalSpeed is reached.");
        TICK_ACCELERATION = builder.defineInRange("tickAcceleration", 0.08D, 0.0D, 2.0D);

        builder.comment("Exhaustion applied once when a Charge begins.");
        ACTIVATION_EXHAUSTION = builder.defineInRange("activationExhaustion", 0.35D, 0.0D, 20.0D);

        builder.comment("Exhaustion applied every tick while a Charge is active.");
        TICK_EXHAUSTION = builder.defineInRange("tickExhaustion", 0.025D, 0.0D, 20.0D);

        builder.comment("Additional exhaustion applied when a Charge damages an entity.");
        HIT_EXHAUSTION = builder.defineInRange("hitExhaustion", 0.40D, 0.0D, 20.0D);

        builder.comment("Minimum food level required to start or continue a Charge.");
        MIN_FOOD_LEVEL = builder.defineInRange("minimumFoodLevel", 7, 0, 20);

        builder.pop();
        SPEC = builder.build();
    }
    private LungeConfig() {}
}
