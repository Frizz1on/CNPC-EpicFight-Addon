package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.client.gui.GuiStringSelection;
import com.goodbird.cnpcefaddon.common.NpcPatchReloadListener;
import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import java.util.Iterator;
import java.util.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.gui.model.GuiCreationEntities;
import noppes.npcs.client.gui.model.GuiCreationScreenInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GuiCreationEntities.class},
   priority = 1001
)
public class MixinGuiCreationEntities extends GuiCreationScreenInterface {
   @Unique
   private static final int CNPCEFADDON_CONFIG_LABEL_ID = 9312;
   @Unique
   private static final int CNPCEFADDON_CONFIG_BUTTON_ID = 9302;
   @Unique
   private static final int CNPCEFADDON_ATTACK_AI_LABEL_ID = 9313;
   @Unique
   private static final int CNPCEFADDON_ATTACK_AI_BUTTON_ID = 9303;
   @Unique
   private static final int CNPCEFADDON_ANIM_DAMAGE_LABEL_ID = 9314;
   @Unique
   private static final int CNPCEFADDON_ANIM_DAMAGE_BUTTON_ID = 9304;

   @Unique
   private static String cnpcefaddon$getAttackAiText(IDataDisplay display) {
      return display.isEFAttackAIDisabled() ? "Disabled" : "Enabled";
   }

   @Unique
   private static String cnpcefaddon$getAnimationDamageText(IDataDisplay display) {
      return display.isEFAnimationDamageEnabled() ? "Enabled" : "Disabled";
   }

   public MixinGuiCreationEntities() {
      super((EntityNPCInterface)null);
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      IDataDisplay display = (IDataDisplay)this.npc.display;
      Vector<String> list = new Vector();
      Iterator var3 = NpcPatchReloadListener.AVAILABLE_MODELS.iterator();
      int labelX = this.guiLeft + 124;
      int buttonX = this.guiLeft + 230;
      int row1Y = this.guiTop - 27;
      int row2Y = this.guiTop + 45;
      int row3Y = this.guiTop + 69;

      while(var3.hasNext()) {
         ResourceLocation resLoc = (ResourceLocation)var3.next();
         list.add(resLoc.toString());
      }

      String curName = "Select Config";
      if (display.hasEFModel()) {
         curName = display.getEFModel().toString();
      }

      this.addLabel(new GuiLabel(CNPCEFADDON_CONFIG_LABEL_ID, "EpicFight Config:", labelX, row1Y + 6, 16777215));
      this.addButton(new GuiButtonNop(this, CNPCEFADDON_CONFIG_BUTTON_ID, buttonX, row1Y, 150, 20, curName, (b) -> {
         this.setSubGui(new GuiStringSelection(this, "Selecting epicfight config:", list, (name) -> {
            display.setEFModel(new ResourceLocation(name), false);
            this.getButton(CNPCEFADDON_CONFIG_BUTTON_ID).setDisplayText(name);
         }));
      }));
      this.addLabel(new GuiLabel(CNPCEFADDON_ATTACK_AI_LABEL_ID, "Disable Attack AI:", labelX, row2Y + 6, 16777215));
      this.addButton(new GuiButtonNop(this, CNPCEFADDON_ATTACK_AI_BUTTON_ID, buttonX, row2Y, 150, 20, cnpcefaddon$getAttackAiText(display), (b) -> {
         display.setEFAttackAIDisabled(!display.isEFAttackAIDisabled(), false);
         this.getButton(CNPCEFADDON_ATTACK_AI_BUTTON_ID).setDisplayText(cnpcefaddon$getAttackAiText(display));
      }));
      this.addLabel(new GuiLabel(CNPCEFADDON_ANIM_DAMAGE_LABEL_ID, "Script Anim Damage:", labelX, row3Y + 6, 16777215));
      this.addButton(new GuiButtonNop(this, CNPCEFADDON_ANIM_DAMAGE_BUTTON_ID, buttonX, row3Y, 150, 20, cnpcefaddon$getAnimationDamageText(display), (b) -> {
         display.setEFAnimationDamageEnabled(!display.isEFAnimationDamageEnabled(), false);
         this.getButton(CNPCEFADDON_ANIM_DAMAGE_BUTTON_ID).setDisplayText(cnpcefaddon$getAnimationDamageText(display));
      }));
   }

   public void drawNpc(GuiGraphics graphics, LivingEntity entity, int x, int y, float zoomed, int rotation) {
      if (this.wrapper.subgui == null) {
         super.drawNpc(graphics, entity, x, y, zoomed, rotation);
      }

   }
}
