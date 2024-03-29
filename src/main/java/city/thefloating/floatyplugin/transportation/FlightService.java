package city.thefloating.floatyplugin.transportation;

import city.thefloating.floatyplugin.FloatyPlugin;
import city.thefloating.floatyplugin.Permission;
import city.thefloating.floatyplugin.soul.Charon;
import com.google.inject.Inject;
import org.bukkit.entity.Player;

/**
 * Checks the players' flight.
 */
public final class FlightService {

  private final Charon charon;
  private final FloatyPlugin plugin;

  @Inject
  public FlightService(
      final Charon charon,
      final FloatyPlugin plugin
  ) {
    this.charon = charon;
    this.plugin = plugin;
  }

  public void checkFlight(final Player player) {
    if (this.canFly(player)) {
      this.enableFlight(player);
    } else {
      this.disableFlight(player);
    }
  }

  public boolean canFly(final Player player) {
    return player.hasPermission(Permission.FLY) && this.charon.grab(player).flyBypassEnabled();
  }

  public void enableFlight(final Player player) {
    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
      if (!player.getAllowFlight()) {
        player.setAllowFlight(true);
      }
    });
  }

  public void disableFlight(final Player player) {
    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
      if (player.getAllowFlight()) {
        player.setAllowFlight(false);
      }
      if (player.isFlying()) {
        player.setFlying(false);
      }
    });
  }

}
