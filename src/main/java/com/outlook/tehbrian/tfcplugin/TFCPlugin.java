package com.outlook.tehbrian.tfcplugin;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.outlook.tehbrian.tfcplugin.commands.ActionCommand;
import com.outlook.tehbrian.tfcplugin.commands.CoreCommand;
import com.outlook.tehbrian.tfcplugin.commands.EmoteCommand;
import com.outlook.tehbrian.tfcplugin.commands.GamemodeCommand;
import com.outlook.tehbrian.tfcplugin.commands.ItemCommand;
import com.outlook.tehbrian.tfcplugin.commands.OntimeCommand;
import com.outlook.tehbrian.tfcplugin.commands.PianoCommand;
import com.outlook.tehbrian.tfcplugin.commands.RulesCommand;
import com.outlook.tehbrian.tfcplugin.commands.UtilCommand;
import com.outlook.tehbrian.tfcplugin.events.AntiBuildEvents;
import com.outlook.tehbrian.tfcplugin.events.BuildingEvents;
import com.outlook.tehbrian.tfcplugin.events.MiscEvents;
import com.outlook.tehbrian.tfcplugin.flight.FlightEvents;
import com.outlook.tehbrian.tfcplugin.piano.PianoEvents;
import me.lucko.luckperms.api.LuckPermsApi;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class TFCPlugin extends JavaPlugin {

    private static TFCPlugin instance;
    private PaperCommandManager commandManager;
    private LuckPermsApi luckPermsApi;

    public TFCPlugin() {
        instance = this;
    }

    public static TFCPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        setupCommandManager();
        setupEvents();
        setupConfig();

        if (!setupLuckPermsApi()) {
            getLogger().severe("No LuckPerms dependency found! Disabling plugin..");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("See you later!");
    }

    private void setupConfig() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new AntiBuildEvents(), this);
        getServer().getPluginManager().registerEvents(new BuildingEvents(this), this);
        getServer().getPluginManager().registerEvents(new MiscEvents(this), this);
        getServer().getPluginManager().registerEvents(new FlightEvents(), this);
        getServer().getPluginManager().registerEvents(new PianoEvents(this), this);
    }

    private void setupCommandManager() {
        commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new ActionCommand(this));
        commandManager.registerCommand(new CoreCommand(this));
        commandManager.registerCommand(new EmoteCommand());
        commandManager.registerCommand(new GamemodeCommand());
        commandManager.registerCommand(new ItemCommand());
        commandManager.registerCommand(new OntimeCommand());
        commandManager.registerCommand(new PianoCommand(this));
        commandManager.registerCommand(new RulesCommand());
        commandManager.registerCommand(new UtilCommand(this));

        commandManager.enableUnstableAPI("help");

        commandManager.getCommandConditions().addCondition(Integer.class, "limits", (context, executionContext, value) -> {
            if (value == null) {
                return;
            }
            if (context.hasConfig("min") && context.getConfigValue("min", 0) > value) {
                throw new ConditionFailedException("Minimum value is " + context.getConfigValue("min", 0) + ".");
            }
            if (context.hasConfig("max") && context.getConfigValue("max", 10) < value) {
                throw new ConditionFailedException("Maximum value is " + context.getConfigValue("max", 10) + ".");
            }
        });
    }

    private boolean setupLuckPermsApi() {
        RegisteredServiceProvider<LuckPermsApi> prsp = getServer().getServicesManager().getRegistration(LuckPermsApi.class);
        if (prsp == null) {
            return false;
        }
        luckPermsApi = prsp.getProvider();
        return luckPermsApi != null;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public LuckPermsApi getLuckPermsApi() {
        return luckPermsApi;
    }
}