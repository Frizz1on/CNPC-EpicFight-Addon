package com.goodbird.cnpcefaddon.common.provider;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.MobPatchPresetProvider;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public class NpcPatchPresetProvider extends MobPatchPresetProvider {
   public NpcPatchPresetProvider(Function<Entity, Supplier<EntityPatch<?>>> presetProvider) {
      super(presetProvider);
   }

   public EntityPatch<?> get(Entity entity) {
      return (EntityPatch)((Supplier)this.presetProvider.apply(entity)).get();
   }
}
