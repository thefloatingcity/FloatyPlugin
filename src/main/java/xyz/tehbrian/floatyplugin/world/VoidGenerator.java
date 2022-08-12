package xyz.tehbrian.floatyplugin.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class VoidGenerator extends ChunkGenerator {

  @Override
  public Location getFixedSpawnLocation(@NotNull final World world, @NotNull final Random random) {
    return new Location(world, 0.5D, 65.0D, 0.5D);
  }

}
