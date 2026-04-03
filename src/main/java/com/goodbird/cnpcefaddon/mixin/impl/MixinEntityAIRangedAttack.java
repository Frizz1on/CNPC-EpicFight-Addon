package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.ai.EntityAIRangedAttack;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(
   value = {EntityAIRangedAttack.class},
   remap = false
)
public class MixinEntityAIRangedAttack {
   @Shadow
   @Final
   private EntityNPCInterface npc;
   private boolean isCurrentlyUsingItem = false;

   @Inject(
      method = {"m_8036_"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cnpcefaddon$cancelCanUseWhenDisabled(CallbackInfoReturnable<Boolean> cir) {
      if (((IDataDisplay)this.npc.display).isEFAttackAIDisabled()) {
         if (this.isCurrentlyUsingItem) {
            this.npc.stopUsingItem();
            this.isCurrentlyUsingItem = false;
         }

         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"m_8037_"},
      at = {@At("HEAD")}
   )
   private void onTickStart(CallbackInfo ci) {
      if (((IDataDisplay)this.npc.display).isEFAttackAIDisabled()) {
         if (this.isCurrentlyUsingItem) {
            this.npc.stopUsingItem();
            this.isCurrentlyUsingItem = false;
         }

         return;
      }

      if (this.npc.stats.ranged.getHasAimAnimation()) {
         boolean hasTarget = this.npc.getTarget() != null;
         if (hasTarget && !this.isCurrentlyUsingItem) {
            ItemStack mainHand = this.npc.getMainHandItem();
            if (mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof CrossbowItem) {
               this.npc.startUsingItem(InteractionHand.MAIN_HAND);
               this.isCurrentlyUsingItem = true;
            }
         } else if (!hasTarget && this.isCurrentlyUsingItem) {
            this.npc.stopUsingItem();
            this.isCurrentlyUsingItem = false;
         }

      }
   }

   @Inject(
      method = {"m_8037_"},
      at = {@At(
   value = "INVOKE",
   target = "Lnoppes/npcs/entity/EntityNPCInterface;m_6504_(Lnet/minecraft/world/entity/LivingEntity;F)V"
)}
   )
   private void onFireRangedAttack(CallbackInfo ci) {
      if (((IDataDisplay)this.npc.display).isEFAttackAIDisabled()) {
         if (this.isCurrentlyUsingItem) {
            this.npc.stopUsingItem();
            this.isCurrentlyUsingItem = false;
         }

         return;
      }

      LivingEntityPatch<?> patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(this.npc, LivingEntityPatch.class);
      if (patch != null) {
         patch.playShootingAnimation();
      }

      if (this.isCurrentlyUsingItem) {
         this.npc.stopUsingItem();
         this.isCurrentlyUsingItem = false;
      }

   }

   @Inject(
      method = {"m_8041_"},
      at = {@At("HEAD")}
   )
   private void cnpcefaddon$stopUsingItemOnGoalStop(CallbackInfo ci) {
      if (this.isCurrentlyUsingItem) {
         this.npc.stopUsingItem();
         this.isCurrentlyUsingItem = false;
      }
   }

   @Redirect(
      method = {"m_8037_"},
      at = @At(
   value = "INVOKE",
   target = "Lnoppes/npcs/entity/EntityNPCInterface;m_6504_(Lnet/minecraft/world/entity/LivingEntity;F)V"
)
   )
   private void cnpcefaddon$redirectRangedAttack(EntityNPCInterface npc, LivingEntity target, float velocity) {
      if (!((IDataDisplay)npc.display).isEFAttackAIDisabled()) {
         npc.performRangedAttack(target, velocity);
      }
   }

   @Shadow
   public boolean hasFired() {
      throw new AssertionError("Mixin failed to shadow hasFired()");
   }
}
