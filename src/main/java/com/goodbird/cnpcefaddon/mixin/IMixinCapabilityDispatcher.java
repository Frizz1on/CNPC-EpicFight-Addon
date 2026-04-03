package com.goodbird.cnpcefaddon.mixin;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IMixinCapabilityDispatcher {
   ICapabilityProvider[] getCaps();

   void setCaps(ICapabilityProvider[] var1);
}
