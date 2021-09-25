package xyz.tehbrian.floatyplugin.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.floatyplugin.config.LangConfig;
import xyz.tehbrian.floatyplugin.util.TimeFormatter;

import java.util.Calendar;

@SuppressWarnings("unused")
public final class JoinQuitListener implements Listener {

    private final LangConfig langConfig;

    @Inject
    public JoinQuitListener(final @NonNull LangConfig langConfig) {
        this.langConfig = langConfig;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.sendMessage(this.langConfig.c(NodePath.path("banner")));

        final Firework firework = player.getWorld().spawn(player.getEyeLocation(), Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();

        if (player.hasPlayedBefore()) {
            event.joinMessage(this.langConfig.c(NodePath.path("join"), Template.of("player", player.displayName())));

            // TODO: use non-deprecated method
            final long millisSinceLastPlayed = Calendar.getInstance().getTimeInMillis() - player.getLastPlayed();
            player.sendMessage(this.langConfig.c(
                    NodePath.path("motd"),
                    Template.of("last", TimeFormatter.fancifyTime(millisSinceLastPlayed))
            ));

            fireworkMeta.addEffect(FireworkEffect.builder()
                    .flicker(true)
                    .trail(false)
                    .with(FireworkEffect.Type.BALL)
                    .withColor(Color.WHITE, Color.BLUE, Color.GREEN)
                    .withFade(Color.GREEN, Color.BLUE, Color.WHITE)
                    .build());
            fireworkMeta.setPower(2);
        } else {
            event.joinMessage(this.langConfig.c(NodePath.path("join_new"), Template.of("player", player.displayName())));

            player.sendMessage(this.langConfig.c(NodePath.path("motd_new"), Template.of("player", player.displayName())));

            fireworkMeta.addEffect(FireworkEffect.builder()
                    .flicker(true)
                    .trail(true)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withColor(Color.SILVER, Color.PURPLE, Color.TEAL)
                    .withFade(Color.TEAL, Color.PURPLE, Color.SILVER)
                    .build());
            fireworkMeta.setPower(3);
        }

        firework.setFireworkMeta(fireworkMeta);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        event.quitMessage(this.langConfig.c(
                NodePath.path("leave"),
                Template.of("player", event.getPlayer().displayName())
        ));
    }

}
