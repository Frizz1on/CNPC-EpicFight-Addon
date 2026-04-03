package com.goodbird.cnpcefaddon.mixin;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

public interface INpcAnimationDamageController {
   void cnpcefaddon$armScriptedAnimationDamage(AssetAccessor<? extends StaticAnimation> animation);
}
