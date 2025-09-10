package alicewritesthings.proton.me.survivalasterisk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue RANDOM_RESPAWNS = BUILDER
            .comment("Randomly determine the player's spawn location when they die?")
            .define("respawnrandomly", true);

    public static final ModConfigSpec.IntValue MIN_RESPAWN_RADIUS = BUILDER
            .comment("How far away should you respawn from your point of death")
            .defineInRange("minrespawndistance", 1000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue MAX_RESPAWN_RADIUS = BUILDER
        .comment("How far away could you respawn from your point of death")
        .defineInRange("maxrespawndistance", 10000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SATURATION_FIXING = BUILDER
        .comment("Regenerate health slower without consuming saturation?")
        .define("noSaturationRegen", true);

    public static final ModConfigSpec.BooleanValue MINIMUM_DAMAGE = BUILDER
        .comment("Cause damage past a certain threshold to deal at least half a heart of damage?")
        .define("dominimumdamage", true);

    public static final ModConfigSpec.IntValue MIN_DAMAGE_THRESHOLD = BUILDER
        .comment("Hits with at least this much base damage will deal at least 1 damage after armor and resistance")
        .defineInRange("mindamagethreshold", 4, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
