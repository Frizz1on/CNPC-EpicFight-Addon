package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import com.goodbird.cnpcefaddon.mixin.IMixinCapabilityDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;

@Mixin(
   value = {DataDisplay.class},
   priority = 1001
)
public class MixinDataDisplay implements IDataDisplay {
   @Shadow(
      remap = false
   )
   EntityNPCInterface npc;
   @Unique
   private ResourceLocation cNPC_EpicFight_Addon$efModelResLoc = null;
   @Unique
   private boolean cNPC_EpicFight_Addon$disableAttackAI = false;
   @Unique
   private boolean cNPC_EpicFight_Addon$animationDamageEnabled = false;
   @Unique
   private float cNPC_EpicFight_Addon$posture = 0.0F;
   @Unique
   private float cNPC_EpicFight_Addon$postureMax = 0.0F;
   @Unique
   private float cNPC_EpicFight_Addon$posturePerParry = 1.0F;

   @Inject(
      method = {"save"},
      at = {@At("HEAD")},
      remap = false
   )
   public void writeToNBT(CompoundTag nbttagcompound, CallbackInfoReturnable<CompoundTag> cir) {
      if (this.hasEFModel()) {
         nbttagcompound.putString("efModel", this.cNPC_EpicFight_Addon$efModelResLoc.toString());
      }

      if (this.cNPC_EpicFight_Addon$disableAttackAI) {
         nbttagcompound.putBoolean("efDisableAttackAI", true);
      }

      if (this.cNPC_EpicFight_Addon$animationDamageEnabled) {
         nbttagcompound.putBoolean("efAnimationDamage", true);
      }

      if (this.cNPC_EpicFight_Addon$posture != 0.0F) {
         nbttagcompound.putFloat("efPosture", this.cNPC_EpicFight_Addon$posture);
      }

      if (this.cNPC_EpicFight_Addon$postureMax > 0.0F) {
         nbttagcompound.putFloat("efPostureMax", this.cNPC_EpicFight_Addon$postureMax);
      }

      if (this.cNPC_EpicFight_Addon$posturePerParry != 1.0F) {
         nbttagcompound.putFloat("efPosturePerParry", this.cNPC_EpicFight_Addon$posturePerParry);
      }

   }

   @Inject(
      method = {"readToNBT"},
      at = {@At("HEAD")},
      remap = false
   )
   public void readFromNBT(CompoundTag nbttagcompound, CallbackInfo ci) {
      if (nbttagcompound.contains("efModel")) {
         this.cNPC_EpicFight_Addon$efModelResLoc = new ResourceLocation(nbttagcompound.getString("efModel"));
         this.cNPC_EpicFight_Addon$updateModelCap();
         if (this.npc.isKilled()) {
            LivingEntityPatch<?> patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(this.npc, LivingEntityPatch.class);
            if (patch != null) {
               patch.onDeath(new LivingDeathEvent(this.npc, this.npc.level().damageSources().genericKill()));
            }
         }
      }

      this.cNPC_EpicFight_Addon$disableAttackAI = nbttagcompound.getBoolean("efDisableAttackAI");
      this.cNPC_EpicFight_Addon$animationDamageEnabled = nbttagcompound.getBoolean("efAnimationDamage");
      this.cNPC_EpicFight_Addon$posture = Math.max(0.0F, nbttagcompound.getFloat("efPosture"));
      this.cNPC_EpicFight_Addon$postureMax = Math.max(0.0F, nbttagcompound.getFloat("efPostureMax"));
      this.cNPC_EpicFight_Addon$posturePerParry = nbttagcompound.contains("efPosturePerParry") ? Math.max(0.0F, nbttagcompound.getFloat("efPosturePerParry")) : 1.0F;

   }

   public void setEFModel(ResourceLocation modelPath, boolean server) {
      this.cNPC_EpicFight_Addon$efModelResLoc = modelPath;
      if (server) {
         this.cNPC_EpicFight_Addon$updateModelCap();
         this.npc.updateClient();
      }

   }

   @Unique
   public ResourceLocation getEFModel() {
      return this.cNPC_EpicFight_Addon$efModelResLoc;
   }

   @Unique
   public boolean hasEFModel() {
      return this.cNPC_EpicFight_Addon$efModelResLoc != null;
   }

   @Unique
   public void setEFAttackAIDisabled(boolean disabled, boolean server) {
      this.cNPC_EpicFight_Addon$disableAttackAI = disabled;
      if (server) {
         if (this.hasEFModel()) {
            this.cNPC_EpicFight_Addon$updateModelCap();
         }

         this.npc.updateClient();
      }
   }

   @Unique
   public boolean isEFAttackAIDisabled() {
      return this.cNPC_EpicFight_Addon$disableAttackAI;
   }

   @Unique
   public void setEFAnimationDamageEnabled(boolean enabled, boolean server) {
      this.cNPC_EpicFight_Addon$animationDamageEnabled = enabled;
      if (server) {
         this.npc.updateClient();
      }
   }

   @Unique
   public boolean isEFAnimationDamageEnabled() {
      return this.cNPC_EpicFight_Addon$animationDamageEnabled;
   }

   @Unique
   public void setEFPosture(float posture, boolean server) {
      this.cNPC_EpicFight_Addon$posture = Math.max(0.0F, posture);
      if (server) {
         this.npc.updateClient();
      }
   }

   @Unique
   public float getEFPosture() {
      return this.cNPC_EpicFight_Addon$posture;
   }

   @Unique
   public void setEFPostureMax(float postureMax, boolean server) {
      this.cNPC_EpicFight_Addon$postureMax = Math.max(0.0F, postureMax);
      if (this.cNPC_EpicFight_Addon$posture > this.cNPC_EpicFight_Addon$postureMax && this.cNPC_EpicFight_Addon$postureMax > 0.0F) {
         this.cNPC_EpicFight_Addon$posture = this.cNPC_EpicFight_Addon$postureMax;
      }

      if (server) {
         this.npc.updateClient();
      }
   }

   @Unique
   public float getEFPostureMax() {
      return this.cNPC_EpicFight_Addon$postureMax;
   }

   @Unique
   public void setEFPosturePerParry(float posturePerParry, boolean server) {
      this.cNPC_EpicFight_Addon$posturePerParry = Math.max(0.0F, posturePerParry);
      if (server) {
         this.npc.updateClient();
      }
   }

   @Unique
   public float getEFPosturePerParry() {
      return this.cNPC_EpicFight_Addon$posturePerParry;
   }

   @Unique
   private void cNPC_EpicFight_Addon$updateModelCap() {
      IMixinCapabilityDispatcher dispatcher = (IMixinCapabilityDispatcher)(Object)((MixinCapabilityProvider)this.npc).invokeGetCapabilities();
      ICapabilityProvider[] caps = dispatcher.getCaps();
      EntityPatchProvider newProvider = new EntityPatchProvider(this.npc);
      EntityPatch entityPatch = newProvider.get();
      if (entityPatch != null) {
         entityPatch.onConstructed(this.npc);
         entityPatch.onJoinWorld(this.npc, new EntityJoinLevelEvent(this.npc, this.npc.level()));
         if (newProvider.hasCapability()) {
            boolean hasFoundAny = false;

            for(int i = 0; i < caps.length; ++i) {
               if (caps[i] instanceof EntityPatchProvider) {
                  caps[i] = newProvider;
                  hasFoundAny = true;
                  break;
               }
            }

            if (!hasFoundAny) {
               ICapabilityProvider[] newCaps = new ICapabilityProvider[caps.length + 1];
               System.arraycopy(caps, 0, newCaps, 0, caps.length);
               newCaps[caps.length] = newProvider;
               dispatcher.setCaps(newCaps);
            }
         }

      }
   }
}
