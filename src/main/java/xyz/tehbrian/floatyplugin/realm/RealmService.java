package xyz.tehbrian.floatyplugin.realm;

import com.google.inject.Inject;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import xyz.tehbrian.floatyplugin.backrooms.BackroomsGenerator;

/**
 * Handles the creation of abstract realms into concrete worlds.
 */
public final class RealmService {

  private final JavaPlugin plugin;
  private final Logger logger;

  @Inject
  public RealmService(final JavaPlugin plugin, final Logger logger) {
    this.plugin = plugin;
    this.logger = logger;
  }

  public World getWorld(final Realm realm) {
    final @Nullable World world = this.plugin.getServer().getWorld(realm.toString());
    if (world == null) {
      throw new RuntimeException("Could not find world for realm `" + realm + "`.");
    }
    return world;
  }

  public Realm getRealm(final World world) {
    return switch (world.getName()) {
      case "madlands" -> Realm.MADLANDS;
      case "overworld" -> Realm.OVERWORLD;
      case "nether" -> Realm.NETHER;
      case "end" -> Realm.END;
      case "backrooms" -> Realm.BACKROOMS;
      default -> throw new RuntimeException("Could not find realm for world `" + world.getName() + "`.");
    };
  }

  public void init() {
    this.createWorlds();
    this.setGameRules();
  }

  private void createWorlds() {
    for (final Realm realm : Realm.values()) {
      if (realm == Realm.MADLANDS) {
        // madlands is created by the server as it is the level-name in server.properties.
        // this can't be disabled, so we'll simply skip over it here.
        continue;
      }

      this.logger.info("Creating world `{}`.", realm.toString());
      final var key = new NamespacedKey(this.plugin, realm.toString());

      final ChunkGenerator generator = switch (realm) {
        case BACKROOMS -> new BackroomsGenerator();
        default -> new VoidGenerator();
      };

      this.plugin.getServer().createWorld(WorldCreator.ofKey(key)
          // prevent black horizon.
          // FLAT worlds turn black below Y-60; NORMAL worlds turn black below Y60.
          .type(WorldType.NORMAL)
          .environment(realm.habitat().environment())
          .generator(generator)
      );
    }
  }

  private void setGameRules() {
    for (final Realm realm : Realm.values()) {
      final World world = this.getWorld(realm);
      world.setGameRule(GameRule.SPAWN_RADIUS, 0);
      world.setGameRule(GameRule.DO_FIRE_TICK, false);
      world.setGameRule(GameRule.MOB_GRIEFING, false);

      // no mob spawning! >:(
      world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
      world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
      world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
      world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
      world.setGameRule(GameRule.DO_INSOMNIA, false);
      world.setGameRule(GameRule.DISABLE_RAIDS, true);

      if (realm == Realm.NETHER) {
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
      } else {
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
      }
    }
  }

  public Location getSpawnPoint(final Realm realm) {
    final var spawn = this.getOrigin(realm);
    spawn.add(0, 0, -3);
    spawn.setPitch(3);
    return spawn;
  }

  private Location getOrigin(final Realm realm) {
    return this.getWorld(realm).getSpawnLocation().add(0.5, 0, 0.5);
  }

}