package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

@Mixin(
   value = {LivingEntityPatch.class},
   remap = false
)
public abstract class MixinLivingEntityPatch {
   @Inject(
      method = {"setLastAttackResult"},
      at = {@At("TAIL")},
      remap = false
   )
   private void cnpcefaddon$trackNpcPostureOnBlockedAttack(AttackResult attackResult, CallbackInfo ci) {
      if (attackResult == null || attackResult.resultType != AttackResult.ResultType.BLOCKED) {
         return;
      }

      LivingEntityPatch<?> patch = (LivingEntityPatch)(Object)this;
      if (!(patch.getOriginal() instanceof EntityNPCInterface npc)) {
         return;
      }

      IDataDisplay display = (IDataDisplay)npc.display;
      float postureMax = display.getEFPostureMax();
      if (postureMax <= 0.0F) {
         return;
      }

      float nextPosture = display.getEFPosture() + Math.max(0.0F, display.getEFPosturePerParry());
      if (nextPosture >= postureMax) {
         display.setEFPosture(0.0F, false);
         patch.applyStun(StunType.NEUTRALIZE, 0.0F);
      } else {
         display.setEFPosture(nextPosture, false);
      }
   }
}
