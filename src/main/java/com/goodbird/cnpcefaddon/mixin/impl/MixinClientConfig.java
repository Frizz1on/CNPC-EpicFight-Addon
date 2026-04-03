package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IClientConfig;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.config.ClientConfig;

@Mixin({ClientConfig.class})
public class MixinClientConfig implements IClientConfig {
   @Shadow(
      remap = false
   )
   @Final
   private static Builder BUILDER;
   @Unique
   private static BooleanValue firstPersonRenderEnabled;

   @Unique
   public boolean isFPRenderEnabled() {
      return (Boolean)firstPersonRenderEnabled.get();
   }

   static {
      firstPersonRenderEnabled = BUILDER.define("ingame.firstPersonRenderEnabled", () -> {
         return true;
      });
   }
}
