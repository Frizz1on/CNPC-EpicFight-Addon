package com.goodbird.cnpcefaddon.client.render;

import com.goodbird.cnpcefaddon.mixin.impl.IMixinRenderEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

public class RenderStorage {
   public static Map<ResourceLocation, PatchedEntityRenderer> renderersMap = new HashMap();

   public static void registerRenderer(ResourceLocation resourceLocation, String renderer) {
      IMixinRenderEngine renderEngine = (IMixinRenderEngine)ClientEngine.getInstance().renderEngine;
      if (!"".equals(renderer)) {
         if ("player".equals(renderer)) {
            renderersMap.put(resourceLocation, renderEngine.getBasicHumanoidRenderer());
         } else {
            EntityType<?> presetEntityType = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(renderer));
            if (!renderEngine.getEntityRendererProvider().containsKey(presetEntityType)) {
               throw new IllegalArgumentException("Datapack Mob Patch Crash: Invalid Renderer type " + renderer);
            }

            renderersMap.put(resourceLocation, (PatchedEntityRenderer)((Function)renderEngine.getEntityRendererProvider().get(presetEntityType)).apply(presetEntityType));
         }

      }
   }
}
