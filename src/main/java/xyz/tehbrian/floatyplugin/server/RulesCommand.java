package xyz.tehbrian.floatyplugin.server;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.paper.cloud.PaperCloudCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.floatyplugin.LuckPermsService;
import xyz.tehbrian.floatyplugin.Permission;
import xyz.tehbrian.floatyplugin.config.BookDeserializer;
import xyz.tehbrian.floatyplugin.config.BooksConfig;
import xyz.tehbrian.floatyplugin.config.LangConfig;

public final class RulesCommand extends PaperCloudCommand<CommandSender> {

  private final BooksConfig booksConfig;
  private final LuckPermsService luckPermsService;
  private final LangConfig langConfig;

  @Inject
  public RulesCommand(
      final BooksConfig booksConfig,
      final LuckPermsService luckPermsService,
      final LangConfig langConfig
  ) {
    this.booksConfig = booksConfig;
    this.luckPermsService = luckPermsService;
    this.langConfig = langConfig;
  }

  @Override
  public void register(final PaperCommandManager<CommandSender> commandManager) {
    final var main = commandManager.commandBuilder("rules")
        .meta(CommandMeta.DESCRIPTION, "The rules for the server.");

    final var page = main
        .argument(IntegerArgument.<CommandSender>builder("page")
            .withMin(1)
            .withMax(BookDeserializer.pageCount(this.bookNode())) // FIXME: won't work with plugin reload
            .asOptionalWithDefault(1)
            .build())
        .handler(c -> c.getSender().sendMessage(
            BookDeserializer.deserializePage(this.bookNode(), c.<Integer>get("page"))
        ));

    final var accept = main.literal("accept", ArgumentDescription.of("Whew, that was a lot of reading."))
        .senderType(Player.class)
        .handler(c -> {
          final Player sender = (Player) c.getSender();
          if (sender.hasPermission(Permission.BUILD_MADLANDS)) {
            sender.sendMessage(this.langConfig.c(NodePath.path("rules", "already_accepted")));
          } else {
            this.luckPermsService.promoteInTrack(sender, "player");
            sender.sendMessage(this.langConfig.c(NodePath.path("rules", "accept")));
          }
        });

    commandManager.command(main);
    commandManager.command(page);
    commandManager.command(accept);
  }

  private CommentedConfigurationNode bookNode() {
    return this.booksConfig.rootNode().node("rules");
  }

}