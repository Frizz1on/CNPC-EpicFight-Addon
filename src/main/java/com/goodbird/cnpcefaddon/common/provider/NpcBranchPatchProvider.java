package com.goodbird.cnpcefaddon.common.provider;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.AbstractMobPatchProvider;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.NullPatchProvider;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public class NpcBranchPatchProvider extends AbstractMobPatchProvider {
   private final List<Pair<ResourceLocation, AbstractMobPatchProvider>> providers = new ArrayList<>();
   private final AbstractMobPatchProvider defaultProvider = new NullPatchProvider();

   public void addProvider(ResourceLocation resLoc, AbstractMobPatchProvider newProv) {
      this.providers.add(Pair.of(resLoc, newProv));
   }

   public EntityPatch<?> get(Entity entity) {
      if (!(entity instanceof EntityNPCInterface npc) || npc.display == null) {
         return this.defaultProvider.get(entity);
      }

      IDataDisplay display = (IDataDisplay)npc.display;
      if (!display.hasEFModel()) {
         return this.defaultProvider.get(entity);
      }

      ResourceLocation model = display.getEFModel();
      for (Pair<ResourceLocation, AbstractMobPatchProvider> providerEntry : this.providers) {
         if (providerEntry.getFirst().equals(model)) {
            return providerEntry.getSecond().get(entity);
         }
      }

      return this.defaultProvider.get(entity);
   }
}
