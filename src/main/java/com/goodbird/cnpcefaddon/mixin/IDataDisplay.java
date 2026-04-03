package com.goodbird.cnpcefaddon.mixin;

import net.minecraft.resources.ResourceLocation;

public interface IDataDisplay {
   default void setEFModel(ResourceLocation modelPath) {
      this.setEFModel(modelPath, true);
   }

   void setEFModel(ResourceLocation var1, boolean var2);

   ResourceLocation getEFModel();

   boolean hasEFModel();

   default void setEFAttackAIDisabled(boolean disabled) {
      this.setEFAttackAIDisabled(disabled, true);
   }

   void setEFAttackAIDisabled(boolean var1, boolean var2);

   boolean isEFAttackAIDisabled();

   default void setEFAnimationDamageEnabled(boolean enabled) {
      this.setEFAnimationDamageEnabled(enabled, true);
   }

   void setEFAnimationDamageEnabled(boolean var1, boolean var2);

   boolean isEFAnimationDamageEnabled();

   default void setEFPosture(float posture) {
      this.setEFPosture(posture, true);
   }

   void setEFPosture(float var1, boolean var2);

   float getEFPosture();

   default void setEFPostureMax(float postureMax) {
      this.setEFPostureMax(postureMax, true);
   }

   void setEFPostureMax(float var1, boolean var2);

   float getEFPostureMax();

   default void setEFPosturePerParry(float posturePerParry) {
      this.setEFPosturePerParry(posturePerParry, true);
   }

   void setEFPosturePerParry(float var1, boolean var2);

   float getEFPosturePerParry();
}
