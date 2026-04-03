package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import com.goodbird.cnpcefaddon.mixin.INpcAnimationDamageController;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin({EntityNPCInterface.class})
public class MixinEntityNpcInterface extends PathfinderMob implements INpcAnimationDamageController {
   @Shadow(
      remap = false
   )
   public DataDisplay display;
   @Shadow(
      remap = false
   )
   public DataStats stats;
   @Unique
   private AssetAccessor<? extends StaticAnimation> cnpcefaddon$scriptedDamageAnimation;
   @Unique
   private float[] cnpcefaddon$scriptedDamageTriggerTimes = new float[0];
   @Unique
   private int cnpcefaddon$nextScriptedDamageIndex = 0;

   protected MixinEntityNpcInterface(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
      super(p_21683_, p_21684_);
   }

   @Inject(
      method = {"addRegularEntries"},
      at = {@At("TAIL")},
      remap = false
   )
   public void addRegularEntries(CallbackInfo ci) {
      LivingEntityPatch<?> patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
      if (patch instanceof HumanoidMobPatch) {
         boolean disableAttackAI = ((IDataDisplay)this.display).isEFAttackAIDisabled();
         ((HumanoidMobPatch)patch).setAIAsInfantry(disableAttackAI || this.getMainHandItem().getItem() instanceof ProjectileWeaponItem);
      }

   }

   @Inject(
      method = {"m_8107_"},
      at = {@At("TAIL")},
      remap = false
   )
   private void cnpcefaddon$tickScriptedAnimationDamage(CallbackInfo ci) {
      if (this.level().isClientSide() || this.cnpcefaddon$scriptedDamageAnimation == null) {
         return;
      }

      if (!((IDataDisplay)this.display).isEFAnimationDamageEnabled()) {
         this.cnpcefaddon$resetScriptedAnimationDamage();
         return;
      }

      LivingEntityPatch<?> patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
      if (patch == null) {
         this.cnpcefaddon$resetScriptedAnimationDamage();
         return;
      }

      AnimationPlayer player = patch.getServerAnimator().animationPlayer;
      AssetAccessor<? extends StaticAnimation> currentAnimation = player.getRealAnimation();
      if (currentAnimation == null || currentAnimation.isEmpty()) {
         if (player.isEnd()) {
            this.cnpcefaddon$resetScriptedAnimationDamage();
         }

         return;
      }

      if (!currentAnimation.registryName().equals(this.cnpcefaddon$scriptedDamageAnimation.registryName())) {
         if (player.isEnd()) {
            this.cnpcefaddon$resetScriptedAnimationDamage();
         }

         return;
      }

      float elapsed = player.getElapsedTime();

      while(this.cnpcefaddon$nextScriptedDamageIndex < this.cnpcefaddon$scriptedDamageTriggerTimes.length && elapsed >= this.cnpcefaddon$scriptedDamageTriggerTimes[this.cnpcefaddon$nextScriptedDamageIndex]) {
         this.cnpcefaddon$performScriptedAnimationHit(patch);
         ++this.cnpcefaddon$nextScriptedDamageIndex;
      }

      if (player.isEnd()) {
         this.cnpcefaddon$resetScriptedAnimationDamage();
      }
   }

   @Unique
   public void cnpcefaddon$armScriptedAnimationDamage(AssetAccessor<? extends StaticAnimation> animation) {
      if (animation == null || animation.isEmpty()) {
         this.cnpcefaddon$resetScriptedAnimationDamage();
         return;
      }

      StaticAnimation resolvedAnimation = (StaticAnimation)animation.get();
      if (resolvedAnimation instanceof AttackAnimation attackAnimation && attackAnimation.phases.length > 0) {
         this.cnpcefaddon$scriptedDamageTriggerTimes = new float[attackAnimation.phases.length];

         for(int i = 0; i < attackAnimation.phases.length; ++i) {
            this.cnpcefaddon$scriptedDamageTriggerTimes[i] = attackAnimation.phases[i].contact;
         }
      } else {
         float totalTime = Math.max(resolvedAnimation.getTotalTime(), 0.1F);
         this.cnpcefaddon$scriptedDamageTriggerTimes = new float[]{totalTime * 0.5F};
      }

      this.cnpcefaddon$scriptedDamageAnimation = animation;
      this.cnpcefaddon$nextScriptedDamageIndex = 0;
   }

   @Unique
   private void cnpcefaddon$performScriptedAnimationHit(LivingEntityPatch<?> patch) {
      LivingEntity target = this.getTarget();
      if (target == null || !target.isAlive()) {
         return;
      }

      if (this.cnpcefaddon$scriptedDamageAnimation.get() instanceof AttackAnimation && (!patch.getCurrentlyAttackTriedEntities().isEmpty() || !patch.getCurrentlyActuallyHitEntities().isEmpty())) {
         return;
      }

      double targetY = target.getY();
      if (target.getBoundingBox() != null) {
         targetY = target.getBoundingBox().maxY;
      }

      double attackDistance = this.distanceToSqr(target.getX(), targetY, target.getZ());
      double maxAttackDistance = (double)(this.stats.melee.getRange() * this.stats.melee.getRange()) + (double)target.getBbWidth();
      double boundingBoxDistance = (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F) + (double)target.getBbWidth();
      if (boundingBoxDistance > maxAttackDistance) {
         maxAttackDistance = boundingBoxDistance;
      }

      if (attackDistance <= maxAttackDistance && (((EntityNPCInterface)(Object)this).canNpcSee(target) || attackDistance < boundingBoxDistance)) {
         this.swing(InteractionHand.MAIN_HAND);
         this.doHurtTarget(target);
      }
   }

   @Unique
   private void cnpcefaddon$resetScriptedAnimationDamage() {
      this.cnpcefaddon$scriptedDamageAnimation = null;
      this.cnpcefaddon$scriptedDamageTriggerTimes = new float[0];
      this.cnpcefaddon$nextScriptedDamageIndex = 0;
   }
}
