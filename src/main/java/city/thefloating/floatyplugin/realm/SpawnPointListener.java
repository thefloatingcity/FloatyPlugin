package city.thefloating.floatyplugin.realm;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.google.inject.Inject;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Objects;

public final class SpawnPointListener implements Listener {

  private final WorldService worldService;
  private final PdcLocStore pdcLocStore;

  @Inject
  public SpawnPointListener(
      final WorldService worldService,
      final PdcLocStore pdcLocStore
  ) {
    this.worldService = worldService;
    this.pdcLocStore = pdcLocStore;
  }

  /**
   * Teleports players to their realm-specific spawn point on respawn.
   */
  @EventHandler
  public void onRespawn(final PlayerRespawnEvent event) {
    final Player player = event.getPlayer();
    final Realm current = Realm.from(player.getWorld());
    event.setRespawnLocation(Objects.requireNonNullElseGet(
        this.getSpawn(player, current),
        () -> this.worldService.getSpawnPoint(current)
    ));
  }

  /**
   * Sets the player's spawn point on deep sleep.
   */
  @EventHandler
  public void onDeepSleep(final PlayerDeepSleepEvent event) {
    final Player player = event.getPlayer();
    player.showTitle(Title.title(
            Component.text("spawn point set").color(NamedTextColor.LIGHT_PURPLE),
            Component.text()
                .append(Component.text("for the ").color(NamedTextColor.GRAY))
                .append(Component.text(Realm.from(player.getWorld()).toString()).color(NamedTextColor.GOLD))
                .build(),
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))
        )
    );
    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 1, true, false, false));
    player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, Sound.Source.MASTER, 1F, 1.3F));
    this.setSpawn(player, player.getLocation());
  }

  /**
   * Prevents sleeping in beds from skipping the night.
   */
  @EventHandler
  public void onTimeSkip(final TimeSkipEvent event) {
    if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
      event.setCancelled(true);
    }
  }

  /**
   * Allows beds in the nether and end.
   */
  @EventHandler
  public void onBedEnter(final PlayerBedEnterEvent event) {
    if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE) {
      event.setUseBed(Event.Result.ALLOW);
    }
  }

  /**
   * Prevent spawn from being set by means other than commands and plugins.
   */
  @EventHandler
  public void onSetSpawn(final PlayerSetSpawnEvent event) {
    event.setNotifyPlayer(false);
    event.setLocation(null);
    if (event.getCause() == PlayerSetSpawnEvent.Cause.COMMAND
        || event.getCause() == PlayerSetSpawnEvent.Cause.PLUGIN) {
      this.setSpawn(event.getPlayer(), event.getLocation());
    }
  }

  private @Nullable Location getSpawn(final Player player, final Realm realm) {
    final PdcLocStore.WorldlessLocation wLoc = this.pdcLocStore.getLocation(player, this.spawnKey(realm));
    if (wLoc == null) {
      return null;
    }
    return new Location(
        this.worldService.getWorld(realm),
        wLoc.x(), wLoc.y(), wLoc.z(),
        wLoc.yaw(), wLoc.pitch()
    );
  }

  private void setSpawn(final Player player, final Location location) {
    this.pdcLocStore.setLocation(player, this.spawnKey(Realm.from(location)), location);
  }

  private NamespacedKey spawnKey(final Realm realm) {
    return this.pdcLocStore.key("spawn-" + realm.toString());
  }

}