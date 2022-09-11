package legend.game;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import legend.core.input.GamepadInputsEnum;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public final class Config {
  private Config() { }

  public static final Object2IntMap<GamepadInputsEnum> KEY_MAP = new Object2IntArrayMap<>();

  static {
    KEY_MAP.put(GamepadInputsEnum.UP, GLFW_KEY_UP);
    KEY_MAP.put(GamepadInputsEnum.DOWN, GLFW_KEY_DOWN);
    KEY_MAP.put(GamepadInputsEnum.LEFT, GLFW_KEY_LEFT);
    KEY_MAP.put(GamepadInputsEnum.RIGHT, GLFW_KEY_RIGHT);
    KEY_MAP.put(GamepadInputsEnum.CROSS, GLFW_KEY_S);
    KEY_MAP.put(GamepadInputsEnum.CIRCLE, GLFW_KEY_D);
    KEY_MAP.put(GamepadInputsEnum.TRIANGLE, GLFW_KEY_W);
    KEY_MAP.put(GamepadInputsEnum.SQUARE, GLFW_KEY_A);
  }

  public static boolean exists() {
    return Files.exists(Paths.get(".", "config.yaml"));
  }
}
