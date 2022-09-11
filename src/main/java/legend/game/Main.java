package legend.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import legend.core.Hardware;
import legend.game.config.Config;
import legend.game.config.ConfigWindow;
import legend.game.debugger.Debugger;
import legend.game.modding.events.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;

import java.util.function.Supplier;

public final class Main {
  static {
    System.setProperty("log4j.skipJansi", "false");
    PluginManager.addPackage("legend");
  }

  private static final Logger LOGGER = LogManager.getFormatterLogger(Main.class);

  private Main() { }

  public static void main(final String[] args) {
    EventManager.INSTANCE.getClass(); // Trigger load

    if(!Config.exists()) {
      launchWindow(ConfigWindow.class, ConfigWindow::new);
    }

    Hardware.start();
  }

  private static boolean hasLaunchedWindow;

  public synchronized static <T extends Application> void launchWindow(final Class<T> cls, final Supplier<T> constructor) {
    if(!hasLaunchedWindow) {
      try {
        hasLaunchedWindow = true;
        Platform.setImplicitExit(false);
        new Thread(() -> Application.launch(cls)).start();
      } catch(final Exception e) {
        LOGGER.info("Failed to launch window", e);
      }
    } else {
      Platform.runLater(() -> {
        try {
          constructor.get().start(new Stage());
        } catch(Exception e) {
          LOGGER.info("Failed to launch window", e);
        }
      });
    }
  }
}
