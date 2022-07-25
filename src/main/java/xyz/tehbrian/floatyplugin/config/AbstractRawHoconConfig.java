package xyz.tehbrian.floatyplugin.config;

import dev.tehbrian.tehlib.configurate.AbstractRawConfig;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public abstract class AbstractRawHoconConfig extends AbstractRawConfig<HoconConfigurateWrapper> {

    /**
     * @param file the config file
     */
    public AbstractRawHoconConfig(final @NonNull Path file) {
        super(new HoconConfigurateWrapper(file, HoconConfigurationLoader.builder()
                .path(file)
                .defaultOptions(opts -> opts.implicitInitialization(false))
                .build()));
    }

}
