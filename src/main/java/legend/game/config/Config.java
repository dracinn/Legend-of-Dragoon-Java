package legend.game.config;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Config {
  private Config() { }

  public static boolean exists() {
    return Files.exists(Paths.get(".", "config.yaml"));
  }
}
