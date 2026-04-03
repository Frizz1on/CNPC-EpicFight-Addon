package com.goodbird.cnpcefaddon.client.gui;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiStringSlotNop;

public class GuiStringSelection extends GuiNPCInterface {
   public GuiStringSlotNop slot;
   public Consumer<String> action;
   public Screen parent;
   public String title;
   public List<String> options;

   public GuiStringSelection(Screen parent, String title, List<String> options, Consumer<String> action) {
      this.drawDefaultBackground = false;
      this.parent = parent;
      this.action = action;
      this.title = title;
      this.options = options;
   }

   public void init() {
      super.init();
      this.addLabel(new GuiLabel(0, this.title, this.width / 2 - this.font.width(this.title) / 2, 20, 16777215));
      this.options.sort(String.CASE_INSENSITIVE_ORDER);
      this.slot = new GuiStringSlotNop(this.options, this, false);
      this.addRenderableWidget(this.slot);
      this.addButton(new GuiButtonNop(this, 2, this.width / 2 - 100, this.height - 44, 98, 20, "gui.back"));
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.slot.render(graphics, mouseX, mouseY, partialTicks);
      super.render(graphics, mouseX, mouseY, partialTicks);
   }

   public void doubleClicked() {
      this.action.accept(this.slot.getSelectedString());
      this.close();
   }

   public void buttonEvent(GuiButtonNop guibutton) {
      int id = guibutton.id;
      if (id == 2) {
         this.close();
      }

   }
}
