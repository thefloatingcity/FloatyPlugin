package xyz.tehbrian.floatyplugin.world;

import com.google.inject.Inject;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public final class WorldService {

    private final JavaPlugin plugin;

    @Inject
    public WorldService(final @NonNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.createWorlds();
        this.setSpawnLocations();
        this.setGameRules();
    }

    public void createWorlds() {
        for (final FloatingWorld floatingWorld : FloatingWorld.values()) {
            if (floatingWorld == FloatingWorld.MADLANDS) {
                continue; // Madlands is generated by Bukkit as it is the level-name in server.properties
            }

            this.plugin.getLog4JLogger().info("Generating world {}", floatingWorld.bukkitName());
            final @NonNull NamespacedKey key = new NamespacedKey(this.plugin, floatingWorld.bukkitName());
            this.plugin.getServer().createWorld(
                    WorldCreator
                            .ofNameAndKey(floatingWorld.bukkitName(), key)
                            .environment(floatingWorld.environment())
                            .generator(new VoidGenerator())
            );
        }
    }

    public @NonNull World getWorld(final FloatingWorld floatingWorld) {
        final @Nullable World world = this.plugin.getServer().getWorld(floatingWorld.bukkitName());
        if (world == null) {
            throw new RuntimeException("Tried to get world, but it didn't exist. Something has gone terribly wrong.");
        }
        return world;
    }

    public @NonNull FloatingWorld getFloatingWorld(final World world) {
        return switch (world.getName()) {
            case "madlands" -> FloatingWorld.MADLANDS;
            case "overworld" -> FloatingWorld.OVERWORLD;
            case "nether" -> FloatingWorld.NETHER;
            case "end" -> FloatingWorld.END;
            default -> throw new IllegalStateException("Unexpected value: " + world.getName());
        };
    }

    public void setGameRules() {
        for (final World world : this.plugin.getServer().getWorlds()) {
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, true);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);

            if (world.getEnvironment() == World.Environment.NETHER) {
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
                world.setGameRule(GameRule.KEEP_INVENTORY, false);
            } else {
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
                world.setGameRule(GameRule.KEEP_INVENTORY, true);
            }
        }
    }

    public @NonNull Location getPlayerSpawnLocation(final FloatingWorld floatingWorld) {
        return new Location(this.getWorld(floatingWorld), 0.5, 65, -3.5, 0, 3);
    }

    public @NonNull Location getSpawnLocation(final FloatingWorld floatingWorld) {
        return new Location(this.getWorld(floatingWorld), 0.5, 65, 0.5, 0, 0);
    }

    public void setSpawnLocations() {
        this.getWorld(FloatingWorld.MADLANDS).setSpawnLocation(this.getSpawnLocation(FloatingWorld.MADLANDS));
        this.getWorld(FloatingWorld.OVERWORLD).setSpawnLocation(this.getSpawnLocation(FloatingWorld.OVERWORLD));
        this.getWorld(FloatingWorld.NETHER).setSpawnLocation(this.getSpawnLocation(FloatingWorld.NETHER));
        this.getWorld(FloatingWorld.END).setSpawnLocation(this.getSpawnLocation(FloatingWorld.END));
    }

    public @NonNull ChunkGenerator getDefaultWorldGenerator(
            @NotNull final String worldName,
            @Nullable final String id
    ) {
        return new VoidGenerator();
    }

}
