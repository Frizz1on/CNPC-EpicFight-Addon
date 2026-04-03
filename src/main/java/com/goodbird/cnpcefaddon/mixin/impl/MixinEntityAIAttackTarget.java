package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import noppes.npcs.ai.EntityAIAttackTarget;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {EntityAIAttackTarget.class},
   remap = false
)
public class MixinEntityAIAttackTarget {
   @Shadow
   @Final
   private EntityNPCInterface npc;
   @Shadow
   private int attackTick;

   @Inject(
      method = {"m_8036_"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cnpcefaddon$cancelCanUseWhenDisabled(CallbackInfoReturnable<Boolean> cir) {
      if (((IDataDisplay)this.npc.display).isEFAttackAIDisabled()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"m_8045_"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cnpcefaddon$cancelCanContinueWhenDisabled(CallbackInfoReturnable<Boolean> cir) {
      if (((IDataDisplay)this.npc.display).isEFAttackAIDisabled()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"m_8037_"},
      at = {@At(
   value = "INVOKE",
   target = "Lnoppes/npcs/entity/EntityNPCInterface;m_6674_(Lnet/minecraft/world/InteractionHand;)V"
)},
      cancellable = true
   )
   private void cnpcefaddon$cancelMeleeAttack(CallbackInfo ci) {
      if (((IDataDisplay)this.npc.display).isEFAttackAIDisabled()) {
         this.attackTick = this.npc.stats.melee.getDelay();
         ci.cancel();
      }
   }
}
