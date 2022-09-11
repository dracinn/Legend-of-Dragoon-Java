package legend.game;

import legend.core.opengl.Gui;
import legend.core.opengl.GuiManager;
import org.lwjgl.system.MemoryStack;

public class ConfigGui extends Gui {
  @Override
  protected void draw(final GuiManager manager, final MemoryStack stack) {
    this.window(manager, stack, "Controls", 0, 0, manager.window.getWidth(), manager.window.getHeight(), () -> {
      this.row(manager, 50, 1);
      this.label(manager, "This is a test");
    });
  }
}
