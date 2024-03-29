package city.thefloating.floatyplugin.inject;

import city.thefloating.floatyplugin.LuckPermsService;
import city.thefloating.floatyplugin.config.BooksConfig;
import city.thefloating.floatyplugin.config.ConfigConfig;
import city.thefloating.floatyplugin.config.EmotesConfig;
import city.thefloating.floatyplugin.config.LangConfig;
import city.thefloating.floatyplugin.config.PianoNotesConfig;
import city.thefloating.floatyplugin.nextbot.Nate;
import city.thefloating.floatyplugin.realm.Transposer;
import city.thefloating.floatyplugin.realm.WorldService;
import city.thefloating.floatyplugin.soul.Charon;
import city.thefloating.floatyplugin.soul.Otzar;
import city.thefloating.floatyplugin.tag.TagGame;
import city.thefloating.floatyplugin.transportation.FlightService;
import city.thefloating.floatyplugin.transportation.PortalListener;
import com.google.inject.AbstractModule;

public final class SingletonModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(FlightService.class).asEagerSingleton();
    this.bind(LuckPermsService.class).asEagerSingleton();
    this.bind(TagGame.class).asEagerSingleton();
    this.bind(Charon.class).asEagerSingleton();
    this.bind(WorldService.class).asEagerSingleton();
    this.bind(Transposer.class).asEagerSingleton();
    this.bind(Nate.class).asEagerSingleton();
    this.bind(PortalListener.class).asEagerSingleton();
    this.bind(BooksConfig.class).asEagerSingleton();
    this.bind(ConfigConfig.class).asEagerSingleton();
    this.bind(EmotesConfig.class).asEagerSingleton();
    this.bind(PianoNotesConfig.class).asEagerSingleton();
    this.bind(LangConfig.class).asEagerSingleton();
    this.bind(Otzar.class).asEagerSingleton();
  }

}
