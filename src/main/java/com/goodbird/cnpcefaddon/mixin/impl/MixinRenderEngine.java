package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.client.render.RenderStorage;
import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

@Mixin({RenderEngine.class})
public class MixinRenderEngine {
   @Inject(
      method = {"getEntityRenderer"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void getEntityRenderer(Entity entity, CallbackInfoReturnable<PatchedEntityRenderer> cir) {
      if (entity instanceof EntityNPCInterface) {
         EntityNPCInterface npc = (EntityNPCInterface)entity;
         IDataDisplay dataDisplay = (IDataDisplay)npc.display;
         if (dataDisplay.hasEFModel()) {
            cir.setReturnValue((PatchedEntityRenderer)RenderStorage.renderersMap.get(dataDisplay.getEFModel()));
         }
      }

   }

   @Inject(
      method = {"hasRendererFor"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void hasRendererFor(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof EntityNPCInterface) {
         EntityNPCInterface npc = (EntityNPCInterface)entity;
         if (((IDataDisplay)npc.display).hasEFModel()) {
            cir.setReturnValue(true);
         }
      }

   }
}
