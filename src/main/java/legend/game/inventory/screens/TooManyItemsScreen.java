package legend.game.inventory.screens;

import legend.core.MathHelper;
import legend.game.inventory.WhichMenu;
import legend.game.types.LodString;
import legend.game.types.MenuItemStruct04;
import legend.game.types.MessageBoxResult;
import legend.game.types.Renderable58;

import java.util.ArrayList;
import java.util.List;

import static legend.game.SItem.Acquired_item_8011c2f8;
import static legend.game.SItem.FUN_801038d4;
import static legend.game.SItem.FUN_80104738;
import static legend.game.SItem.FUN_80104b60;
import static legend.game.SItem.Press_to_sort_8011d024;
import static legend.game.SItem.This_item_cannot_be_thrown_away_8011c2a8;
import static legend.game.SItem._8011c314;
import static legend.game.SItem._8011c32c;
import static legend.game.SItem.allocateUiElement;
import static legend.game.SItem.glyphs_80114548;
import static legend.game.SItem.menuStack;
import static legend.game.SItem.messageBox_8011dc90;
import static legend.game.SItem.renderGlyphs;
import static legend.game.SItem.renderMenuItems;
import static legend.game.SItem.renderString;
import static legend.game.SItem.renderText;
import static legend.game.SMap.FUN_800e3fac;
import static legend.game.Scus94491BpeSegment.free;
import static legend.game.Scus94491BpeSegment.scriptStartEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.itemCantBeDiscarded;
import static legend.game.Scus94491BpeSegment_8002.playSound;
import static legend.game.Scus94491BpeSegment_8002.recalcInventory;
import static legend.game.Scus94491BpeSegment_8002.setInventoryFromDisplay;
import static legend.game.Scus94491BpeSegment_8002.sortItems;
import static legend.game.Scus94491BpeSegment_8002.unloadRenderable;
import static legend.game.Scus94491BpeSegment_8002.uploadRenderables;
import static legend.game.Scus94491BpeSegment_8004.loadingGameStateOverlay_8004dd08;
import static legend.game.Scus94491BpeSegment_8004.mainCallbackIndex_8004dd20;
import static legend.game.Scus94491BpeSegment_800b._800bb168;
import static legend.game.Scus94491BpeSegment_800b.drgn0_6666FilePtr_800bdc3c;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.itemsDroppedByEnemiesCount_800bc978;
import static legend.game.Scus94491BpeSegment_800b.itemsDroppedByEnemies_800bc928;
import static legend.game.Scus94491BpeSegment_800b.saveListDownArrow_800bdb98;
import static legend.game.Scus94491BpeSegment_800b.saveListUpArrow_800bdb94;
import static legend.game.Scus94491BpeSegment_800b.textZ_800bdf00;
import static legend.game.Scus94491BpeSegment_800b.whichMenu_800bdc38;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class TooManyItemsScreen extends MenuScreen<TooManyItemsScreen.State> {
  private State state = State.INIT_1;
  private double scrollAccumulator;
  private int mouseX;
  private int mouseY;

  private int dropIndex;
  private int invIndex;
  private int invScroll;

  private Renderable58 renderable_8011e200;
  private Renderable58 renderable_8011e204;

  private final List<MenuItemStruct04> equipment = new ArrayList<>();
  private final List<MenuItemStruct04> items = new ArrayList<>();
  private final List<MenuItemStruct04> droppedItems = new ArrayList<>();

  @Override
  public MenuId menuId() {
    return MenuId.TOO_MANY_ITEMS;
  }

  @Override
  public State getState() {
    return this.state;
  }

  @Override
  protected void render() {
    switch(this.state) {
      case INIT_1 -> {
        if(!drgn0_6666FilePtr_800bdc3c.isNull()) {
          recalcInventory();
          FUN_80104738(this.equipment, this.items, 0x1L);
          messageBox_8011dc90.state_0c = MessageBoxScreen.State._0;

          for(int itemIndex = 0; itemIndex < itemsDroppedByEnemiesCount_800bc978.get(); itemIndex++) {
            final MenuItemStruct04 item = new MenuItemStruct04();
            item.itemId_00 = itemsDroppedByEnemies_800bc928.get(itemIndex).get();
            this.droppedItems.add(item);
          }

          this.state = State.INIT_2;
        }
      }

      case INIT_2 -> {
        deallocateRenderables(0xff);
        this.invScroll = 0;
        this.invIndex = 0;
        this.dropIndex = 0;

        deallocateRenderables(0);
        this.render(true, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0);
        scriptStartEffect(2, 10);
        this.state = State.REPLACE_MESSAGE_BOX;
      }

      case REPLACE_MESSAGE_BOX -> {
        menuStack.pushScreen(new MessageBoxScreen(new LodString("Too many items. Replace?"), 2, result -> this.state = result == MessageBoxResult.YES ? State.REPLACE_INIT : State.DISCARD));
        this.state = State.ITEMS_TO_DROP_LIST;
      }

      case ITEMS_TO_DROP_LIST ->
        this.render(false, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0);

      case REPLACE_INIT -> {
        this.dropIndex = 0;
        final Renderable58 renderable2 = allocateUiElement(124, 124, 42, this.FUN_8010f178(0));
        this.renderable_8011e200 = renderable2;
        FUN_80104b60(renderable2);
        deallocateRenderables(0);
        this.render(true, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0x1L);
        this.state = State.REPLACE;
      }

      case REPLACE ->
        this.render(false, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0x1L);

      case _9 -> {
        final int slotCount;
        if(this.droppedItems.get(this.dropIndex).itemId_00 < 0xc0) {
          slotCount = gameState_800babc8.equipmentCount_1e4.get();
        } else {
          slotCount = gameState_800babc8.itemCount_1e6.get();
        }

        if(this.scrollAccumulator >= 1.0d) {
          this.scrollAccumulator -= 1.0d;

          if(MathHelper.inBox(this.mouseX, this.mouseY, 188, 42, 171, 119)) {
            if(this.invScroll > 0) {
              playSound(1);
              this.invScroll--;
              this.renderable_8011e204.y_44 = this.FUN_8010f178(this.invIndex);
            }
          }
        }

        if(this.scrollAccumulator <= -1.0d) {
          this.scrollAccumulator += 1.0d;

          if(MathHelper.inBox(this.mouseX, this.mouseY, 188, 42, 171, 119)) {
            if(this.invScroll < slotCount - 7) {
              playSound(1);
              this.invScroll++;
              this.renderable_8011e204.y_44 = this.FUN_8010f178(this.invIndex);
            }
          }
        }

        this.render(false, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0x3L);
      }

      case DISCARD -> {
        this.render(false, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0);

        menuStack.pushScreen(new MessageBoxScreen(new LodString("Discard extra items?"), 2, result -> {
          if(result == MessageBoxResult.YES) {
            for(final MenuItemStruct04 item : this.droppedItems) {
              if(itemCantBeDiscarded(item.itemId_00)) {
                menuStack.pushScreen(new MessageBoxScreen(This_item_cannot_be_thrown_away_8011c2a8, 0, result1 -> this.state = State.REPLACE_INIT));
                return;
              }
            }

            scriptStartEffect(1, 10);
            this.state = State.UNLOAD;
          } else {
            this.state = State.REPLACE_INIT;
          }
        }));

        this.state = State.ITEMS_TO_DROP_LIST;
      }

      case UNLOAD -> {
        this.render(false, this.droppedItems.get(this.dropIndex).itemId_00, this.invIndex, this.invScroll, 0);

        if(_800bb168.get() >= 0xff) {
          scriptStartEffect(2, 10);
          deallocateRenderables(0xff);
          free(drgn0_6666FilePtr_800bdc3c.getPointer());
          whichMenu_800bdc38 = WhichMenu.UNLOAD_TOO_MANY_ITEMS_MENU_35;

          if(mainCallbackIndex_8004dd20.get() == 5 && loadingGameStateOverlay_8004dd08.get() == 0) {
            FUN_800e3fac();
          }

          textZ_800bdf00.set(13);
        }
      }
    }
  }

  private void render(final boolean allocate, final int itemId, final int slotIndex, final int slotScroll, final long a4) {
    if(allocate) {
      renderGlyphs(glyphs_80114548, 0, 0);
      saveListUpArrow_800bdb94 = allocateUiElement(61, 68, 358, this.FUN_8010f178(0));
      saveListDownArrow_800bdb98 = allocateUiElement(53, 60, 358, this.FUN_8010f178(6));
    }

    renderMenuItems(16, 33, this.droppedItems, 0, Math.min(5, itemsDroppedByEnemiesCount_800bc978.get()), saveListUpArrow_800bdb94, saveListDownArrow_800bdb98);

    if((a4 & 0x1L) != 0 && !allocate) {
      renderString(0, 16, 164, itemId, false);
    }

    renderText(Acquired_item_8011c2f8, 32, 22, 4);

    if(itemId >= 0xc0) {
      if(itemId >= 0xff && (a4 & 0x2L) != 0) {
        final Renderable58 renderable = FUN_801038d4(137, 84, 140);
        renderable.clut_30 = 0x7ceb;
        renderText(Press_to_sort_8011d024, 37, 140, 4);
      }

      renderText(_8011c32c, 210, 22, 4);

      if((a4 & 0x1L) != 0) {
        renderMenuItems(194, 33, this.items, slotScroll, 7, saveListUpArrow_800bdb94, saveListDownArrow_800bdb98);
      }

      if((a4 & 0x2L) != 0) {
        renderString(0, 194, 164, this.items.get(slotScroll + slotIndex).itemId_00, allocate);

        if((a4 & 0x2L) != 0) {
          final Renderable58 renderable = FUN_801038d4(137, 84, 140);
          renderable.clut_30 = 0x7ceb;
          renderText(Press_to_sort_8011d024, 37, 140, 4);
        }
      }
    } else {
      renderText(_8011c314, 210, 22, 4);

      if((a4 & 0x1L) != 0) {
        renderMenuItems(194, 33, this.equipment, slotScroll, 7, saveListUpArrow_800bdb94, saveListDownArrow_800bdb98);
      }

      if((a4 & 0x2L) != 0) {
        renderString(0, 194, 164, this.equipment.get(slotScroll + slotIndex).itemId_00, allocate);

        if((a4 & 0x2L) != 0) {
          final Renderable58 renderable = FUN_801038d4(137, 84, 140);
          renderable.clut_30 = 0x7ceb;
          renderText(Press_to_sort_8011d024, 37, 140, 4);
        }
      }
    }

    uploadRenderables();
  }

  private int FUN_8010f178(final int slot) {
    return 42 + slot * 17;
  }

  @Override
  protected void mouseMove(final int x, final int y) {
    this.mouseX = x;
    this.mouseY = y;

    if(this.state == State.REPLACE) {
      for(int i = 0; i < itemsDroppedByEnemiesCount_800bc978.get(); i++) {
        if(this.dropIndex != i && MathHelper.inBox(x, y, 9, this.FUN_8010f178(i), 171, 17)) {
          playSound(1);
          this.dropIndex = i;
          this.renderable_8011e200.y_44 = this.FUN_8010f178(i);
        }
      }
    } else if(this.state == State._9) {
      for(int i = 0; i < 7; i++) {
        if(this.invIndex != i && MathHelper.inBox(x, y, 188, this.FUN_8010f178(i), 171, 17)) {
          playSound(1);
          this.invIndex = i;
          this.renderable_8011e204.y_44 = this.FUN_8010f178(i);
        }
      }
    }
  }

  @Override
  protected void mouseClick(final int x, final int y, final int button, final int mods) {
    if(this.state == State.REPLACE) {
      for(int i = 0; i < itemsDroppedByEnemiesCount_800bc978.get(); i++) {
        if(MathHelper.inBox(x, y, 9, this.FUN_8010f178(i), 171, 17)) {
          playSound(2);
          this.dropIndex = i;
          this.renderable_8011e200.y_44 = this.FUN_8010f178(i);

          this.invScroll = 0;
          this.invIndex = 0;
          final Renderable58 renderable3 = allocateUiElement(118, 118, 220, this.FUN_8010f178(0));
          this.renderable_8011e204 = renderable3;
          FUN_80104b60(renderable3);
          playSound(2);
          this.state = State._9;
        }
      }
    } else if(this.state == State._9) {
      for(int i = 0; i < 7; i++) {
        if(MathHelper.inBox(x, y, 188, this.FUN_8010f178(i), 171, 17)) {
          playSound(2);
          this.invIndex = i;
          this.renderable_8011e204.y_44 = this.FUN_8010f178(i);

          final MenuItemStruct04 newItem = this.droppedItems.get(this.dropIndex);
          final int isItem = this.droppedItems.get(this.dropIndex).itemId_00 >= 0xc0 ? 1 : 0;
          final MenuItemStruct04 existingItem;
          if(isItem == 0) {
            existingItem = this.equipment.get(this.invIndex + this.invScroll);
          } else {
            existingItem = this.items.get(this.invIndex + this.invScroll);
          }

          if((existingItem.flags_02 & 0x6000) != 0) {
            playSound(40);
          } else {
            final int itemId = existingItem.itemId_00;
            final int flags = existingItem.flags_02;

            existingItem.itemId_00 = newItem.itemId_00;
            existingItem.flags_02 = newItem.flags_02;

            newItem.itemId_00 = itemId;
            newItem.flags_02 = flags;

            playSound(2);
            unloadRenderable(this.renderable_8011e204);
            this.state = State.REPLACE;

            if(isItem != 0) {
              setInventoryFromDisplay(this.items, gameState_800babc8.items_2e9, gameState_800babc8.itemCount_1e6.get());
            } else {
              setInventoryFromDisplay(this.equipment, gameState_800babc8.equipment_1e8, gameState_800babc8.equipmentCount_1e4.get());
            }
          }
        }
      }
    }
  }

  @Override
  protected void mouseScroll(final double deltaX, final double deltaY) {
    if(this.state != State._9) {
      return;
    }

    if(this.scrollAccumulator < 0 && deltaY > 0 || this.scrollAccumulator > 0 && deltaY < 0) {
      this.scrollAccumulator = 0;
    }

    this.scrollAccumulator += deltaY;
  }

  @Override
  protected void keyPress(final int key, final int scancode, final int mods) {
    if(mods != 0) {
      return;
    }

    if(key == GLFW_KEY_ESCAPE) {
      if(this.state == State.REPLACE) {
        playSound(3);
        unloadRenderable(this.renderable_8011e200);
        this.state = State.DISCARD;
      } else if(this.state == State._9) {
        playSound(3);
        unloadRenderable(this.renderable_8011e204);
        this.state = State.REPLACE;
      }
    } else if(key == GLFW_KEY_S && this.state == State._9) {
      playSound(2);

      if(this.droppedItems.get(this.dropIndex).itemId_00 < 0xc0) {
        sortItems(this.equipment, gameState_800babc8.equipment_1e8, gameState_800babc8.equipmentCount_1e4.get());
      } else {
        sortItems(this.items, gameState_800babc8.items_2e9, gameState_800babc8.itemCount_1e6.get());
      }
    }
  }

  public enum State {
    INIT_1,
    INIT_2,
    REPLACE_MESSAGE_BOX,
    ITEMS_TO_DROP_LIST,
    REPLACE_INIT,
    REPLACE,
    _9,
    DISCARD,
    UNLOAD,
  }
}
