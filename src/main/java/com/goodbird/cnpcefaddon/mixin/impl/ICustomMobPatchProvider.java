package com.goodbird.cnpcefaddon.mixin.impl;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.CustomMobPatchProvider;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Builder;

@Mixin({CustomMobPatchProvider.class})
public interface ICustomMobPatchProvider {
   @Accessor(
      remap = false
   )
   void setCombatBehaviorsBuilder(Builder<?> var1);

   @Accessor(
      remap = false
   )
   void setDefaultAnimations(List<Pair<LivingMotion, AnimationAccessor<? extends StaticAnimation>>> var1);

   @Accessor(
      remap = false
   )
   void setStunAnimations(Map<StunType, AnimationAccessor<? extends StaticAnimation>> var1);

   @Accessor(
      remap = false
   )
   void setAttributeValues(Object2DoubleMap<Attribute> var1);

   @Accessor(
      remap = false
   )
   void setFaction(Faction var1);

   @Accessor(
      remap = false
   )
   void setChasingSpeed(double var1);

   @Accessor(
      remap = false
   )
   void setScale(float var1);
}
