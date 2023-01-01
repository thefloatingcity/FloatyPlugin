package xyz.tehbrian.floatyplugin.util;

import java.time.Duration;

public final class Ticks {

  private Ticks() {
  }

  public static long in(final Duration duration) {
    return duration.toSeconds() * 20;
  }

}
