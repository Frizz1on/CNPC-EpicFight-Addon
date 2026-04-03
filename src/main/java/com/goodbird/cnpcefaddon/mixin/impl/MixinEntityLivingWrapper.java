package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import com.goodbird.cnpcefaddon.mixin.INpcAnimationDamageController;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.api.wrapper.EntityLivingBaseWrapper;
import noppes.npcs.api.wrapper.EntityWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin({EntityLivingBaseWrapper.class})
public class MixinEntityLivingWrapper<T extends LivingEntity> extends EntityWrapper<T> {
   public MixinEntityLivingWrapper(T entity) {
      super(entity);
   }

   @Unique
   public void playEFAnimation(String animPath) {
      AssetAccessor<? extends StaticAnimation> anim = AnimationManager.byKey(animPath);
      LivingEntityPatch<?> patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(this.entity, LivingEntityPatch.class);
      if (patch == null || anim == null || anim.isEmpty()) {
         return;
      }

      if (!this.entity.level().isClientSide() && this.entity instanceof EntityNPCInterface npc && ((IDataDisplay)npc.display).isEFAnimationDamageEnabled()) {
         ((INpcAnimationDamageController)npc).cnpcefaddon$armScriptedAnimationDamage(anim);
      }

      patch.playAnimationSynchronized(anim, 0.0F);
   }

   @Unique
   public void evasive(String animPath, String direction, double strength, String intent) {
      if (animPath != null && !animPath.isBlank()) {
         this.playEFAnimation(animPath);
      }

      Vec3 impulse = this.cnpcefaddon$resolveEvasiveImpulse(direction, intent, strength);
      if (impulse.lengthSqr() <= 1.0E-6D) {
         return;
      }

      this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(impulse));
      this.entity.hasImpulse = true;
   }

   @Unique
   public void evaisve(String animPath, String direction, double strength, String intent) {
      this.evasive(animPath, direction, strength, intent);
   }

   @Unique
   private Vec3 cnpcefaddon$resolveEvasiveImpulse(String direction, String intent, double strength) {
      double clampedStrength = Math.max(0.0D, strength);
      if (clampedStrength <= 0.0D) {
         return Vec3.ZERO;
      }

      Vec3 forward = this.cnpcefaddon$getHorizontalForward();
      if (forward.lengthSqr() <= 1.0E-6D) {
         return Vec3.ZERO;
      }

      Vec3 backward = forward.scale(-1.0D);
      Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);
      Vec3 left = right.scale(-1.0D);
      String normalizedDirection = direction == null ? "" : direction.trim().toLowerCase();
      String normalizedIntent = intent == null ? "" : intent.trim().toLowerCase();
      Vec3 chosen;

      switch(normalizedDirection) {
      case "forward":
      case "front":
      case "toward":
      case "towards":
      case "chase":
         chosen = forward;
         break;
      case "back":
      case "backward":
      case "backwards":
      case "away":
      case "dodge":
         chosen = backward;
         break;
      case "left":
         chosen = left;
         break;
      case "right":
         chosen = right;
         break;
      case "random":
      case "mixup":
         chosen = this.entity.getRandom().nextBoolean() ? left : right;
         break;
      default:
         switch(normalizedIntent) {
         case "chase":
            chosen = forward;
            break;
         case "step":
            chosen = this.entity.getRandom().nextBoolean() ? left : right;
            break;
         case "dodge":
         default:
            chosen = backward;
         }
      }

      return chosen.normalize().scale(clampedStrength);
   }

   @Unique
   private Vec3 cnpcefaddon$getHorizontalForward() {
      LivingEntity target = this.entity instanceof Mob mob ? mob.getTarget() : null;
      Vec3 forward;
      if (target != null) {
         forward = new Vec3(target.getX() - this.entity.getX(), 0.0D, target.getZ() - this.entity.getZ());
      } else {
         Vec3 look = this.entity.getLookAngle();
         forward = new Vec3(look.x, 0.0D, look.z);
      }

      return forward.lengthSqr() <= 1.0E-6D ? Vec3.ZERO : forward.normalize();
   }
}
