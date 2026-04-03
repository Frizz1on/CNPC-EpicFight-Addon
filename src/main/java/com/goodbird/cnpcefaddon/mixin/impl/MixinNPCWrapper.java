package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.api.wrapper.EntityLivingWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({NPCWrapper.class})
public abstract class MixinNPCWrapper<T extends EntityNPCInterface> extends EntityLivingWrapper<T> {
   public MixinNPCWrapper(T entity) {
      super(entity);
   }

   @Unique
   public void setEFModel(String modelPath) {
      ((IDataDisplay)((EntityNPCInterface)this.entity).display).setEFModel(new ResourceLocation(modelPath));
   }

   @Unique
   public void setEFAttackAI(boolean enabled) {
      ((IDataDisplay)((EntityNPCInterface)this.entity).display).setEFAttackAIDisabled(!enabled);
   }

   @Unique
   public boolean isEFAttackAIEnabled() {
      return !((IDataDisplay)((EntityNPCInterface)this.entity).display).isEFAttackAIDisabled();
   }

   @Unique
   public void setEFAnimationDamage(boolean enabled) {
      ((IDataDisplay)((EntityNPCInterface)this.entity).display).setEFAnimationDamageEnabled(enabled);
   }

   @Unique
   public boolean isEFAnimationDamageEnabled() {
      return ((IDataDisplay)((EntityNPCInterface)this.entity).display).isEFAnimationDamageEnabled();
   }

   @Unique
   public void setEFPostureMax(double postureMax) {
      ((IDataDisplay)((EntityNPCInterface)this.entity).display).setEFPostureMax((float)postureMax);
   }

   @Unique
   public double getEFPostureMax() {
      return (double)((IDataDisplay)((EntityNPCInterface)this.entity).display).getEFPostureMax();
   }

   @Unique
   public void setEFPosturePerParry(double posturePerParry) {
      ((IDataDisplay)((EntityNPCInterface)this.entity).display).setEFPosturePerParry((float)posturePerParry);
   }

   @Unique
   public double getEFPosturePerParry() {
      return (double)((IDataDisplay)((EntityNPCInterface)this.entity).display).getEFPosturePerParry();
   }

   @Unique
   public void setEFPosture(double posture) {
      ((IDataDisplay)((EntityNPCInterface)this.entity).display).setEFPosture((float)posture);
   }

   @Unique
   public double getEFPosture() {
      return (double)((IDataDisplay)((EntityNPCInterface)this.entity).display).getEFPosture();
   }
}
