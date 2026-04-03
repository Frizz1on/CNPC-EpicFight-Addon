package com.goodbird.cnpcefaddon.common.provider;

import com.goodbird.cnpcefaddon.common.patch.NpcPatch;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.CustomMobPatchProvider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public class NpcPatchProvider extends CustomMobPatchProvider implements INpcPatchProvider {
   public Armature armature;

   public EntityPatch<?> get(Entity entity) {
      return new NpcPatch(this.faction, this);
   }

   public void setArmature(Armature armature) {
      this.armature = armature;
   }
}
