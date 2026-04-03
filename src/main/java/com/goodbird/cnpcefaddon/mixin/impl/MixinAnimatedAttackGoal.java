package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;

@Mixin(
   value = {AnimatedAttackGoal.class},
   remap = false
)
public class MixinAnimatedAttackGoal {
   @Shadow(
      remap = false
   )
   @Final
   protected MobPatch<?> mobpatch;

   @Inject(
      method = {"m_8036_"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void cnpcefaddon$cancelGoalWhenNpcAiDisabled(CallbackInfoReturnable<Boolean> cir) {
      if (this.cnpcefaddon$isNpcAttackAiDisabled()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"m_8037_"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void cnpcefaddon$cancelTickWhenNpcAiDisabled(CallbackInfo ci) {
      if (this.cnpcefaddon$isNpcAttackAiDisabled()) {
         ci.cancel();
      }
   }

   private boolean cnpcefaddon$isNpcAttackAiDisabled() {
      if (this.mobpatch == null || !(this.mobpatch.getOriginal() instanceof EntityNPCInterface npc)) {
         return false;
      }

      return ((IDataDisplay)npc.display).isEFAttackAIDisabled();
   }
}
