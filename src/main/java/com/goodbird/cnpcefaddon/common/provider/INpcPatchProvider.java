package com.goodbird.cnpcefaddon.common.provider;

import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public interface INpcPatchProvider {
   void setArmature(Armature var1);

   EntityPatch<?> get(Entity var1);
}
