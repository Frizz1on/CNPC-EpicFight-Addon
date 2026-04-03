package com.goodbird.cnpcefaddon.common.patch;

import com.goodbird.cnpcefaddon.common.provider.NpcHumanoidPatchProvider;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.CustomHumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public class NpcHumanoidPatch<T extends PathfinderMob> extends CustomHumanoidMobPatch<T> implements INpcPatch {
   NpcHumanoidPatchProvider provider;

   public NpcHumanoidPatch(Faction faction, NpcHumanoidPatchProvider provider) {
      super(faction, provider);
      this.provider = provider;
   }

   public void onConstructed(T entityIn) {
      super.onConstructed(entityIn);
      this.armature = this.provider.armature.deepCopy();
   }

   public HumanoidArmature getArmature() {
      return (HumanoidArmature)this.armature;
   }

   public void updateMotion(boolean considerInaction) {
      super.commonAggressiveRangedMobUpdateMotion(considerInaction);
   }
}
