package com.goodbird.cnpcefaddon.common;

import com.goodbird.cnpcefaddon.client.render.RenderStorage;
import com.goodbird.cnpcefaddon.common.network.SPDatapackSync;
import com.goodbird.cnpcefaddon.common.provider.INpcPatchProvider;
import com.goodbird.cnpcefaddon.common.provider.NpcBranchPatchProvider;
import com.goodbird.cnpcefaddon.common.provider.NpcHumanoidPatchProvider;
import com.goodbird.cnpcefaddon.common.provider.NpcPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.ICustomHumanoidMobPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.ICustomMobPatchProvider;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomEntities;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.AbstractMobPatchProvider;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.CustomHumanoidMobPatchProvider;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.MobPatchPresetProvider;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener.NullPatchProvider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;

public class NpcPatchReloadListener extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = (new GsonBuilder()).create();
   public static NpcBranchPatchProvider branchPatchProvider = new NpcBranchPatchProvider();
   public static Set<ResourceLocation> AVAILABLE_MODELS = new HashSet();
   public static Map<ResourceLocation, CompoundTag> TAGMAP = Maps.newHashMap();

   public NpcPatchReloadListener() {
      super(GSON, "npc_epicfight_mobpatch");
   }

   protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
      branchPatchProvider = new NpcBranchPatchProvider();
      AVAILABLE_MODELS = new HashSet();
      TAGMAP = Maps.newHashMap();
      Armatures.registerEntityTypeArmature(CustomEntities.entityCustomNpc, Armatures.BIPED);
      Iterator var4 = objectIn.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<ResourceLocation, JsonElement> entry = (Entry)var4.next();
         CompoundTag tag = null;

         try {
            tag = TagParser.parseTag(((JsonElement)entry.getValue()).toString());
         } catch (CommandSyntaxException var8) {
            var8.printStackTrace();
         }

         branchPatchProvider.addProvider((ResourceLocation)entry.getKey(), deserializeMobPatchProvider(tag, false));
         AVAILABLE_MODELS.add((ResourceLocation)entry.getKey());
         CompoundTag filteredTag = MobPatchReloadListener.filterClientData(tag);
         filteredTag.putString("patchType", "NORMAL");
         TAGMAP.put((ResourceLocation)entry.getKey(), filteredTag);
         EntityPatchProvider.putCustomEntityPatch(CustomEntities.entityCustomNpc, (entity) -> {
            return () -> {
               return branchPatchProvider.get(entity);
            };
         });
         if (EpicFightSharedConstants.isPhysicalClient()) {
            RenderStorage.registerRenderer((ResourceLocation)entry.getKey(), tag.contains("preset") ? tag.getString("preset") : tag.getString("renderer"));
         }
      }

   }

   public static AbstractMobPatchProvider deserializeMobPatchProvider(CompoundTag tag, boolean clientSide) {
      boolean disabled = tag.contains("disabled") && tag.getBoolean("disabled");
      if (disabled) {
         return new NullPatchProvider();
      } else if (tag.contains("preset")) {
         String presetName = tag.getString("preset");
         Function<Entity, Supplier<EntityPatch<?>>> preset = EntityPatchProvider.get(presetName);
         Armatures.registerEntityTypeArmature(CustomEntities.entityCustomNpc, Armatures.BIPED);
         MobPatchPresetProvider mobPatchPresetProvider = new MobPatchPresetProvider(preset);
         return mobPatchPresetProvider;
      } else {
         boolean humanoid = tag.getBoolean("isHumanoid");
         AbstractMobPatchProvider provider = humanoid ? new NpcHumanoidPatchProvider() : new NpcPatchProvider();
         ICustomMobPatchProvider npcPatchProvider = (ICustomMobPatchProvider)provider;
         npcPatchProvider.setAttributeValues(MobPatchReloadListener.deserializeAttributes(tag.getCompound("attributes")));
         ResourceLocation modelLocation = new ResourceLocation(tag.getString("model"));
         ResourceLocation armatureLocation = new ResourceLocation(tag.getString("armature"));
         modelLocation = new ResourceLocation(modelLocation.getNamespace(), "animmodels/" + modelLocation.getPath() + ".json");
         armatureLocation = new ResourceLocation(armatureLocation.getNamespace(), "animmodels/" + armatureLocation.getPath() + ".json");
         if (EpicFightSharedConstants.isPhysicalClient()) {
            Meshes.getOrCreate(modelLocation, (jsonModelLoader) -> {
               return humanoid ? jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new) : jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new);
            }).get();
            Armature armature = (Armature)Armatures.getOrCreate(armatureLocation, humanoid ? HumanoidArmature::new : Armature::new).get();
            ((INpcPatchProvider)provider).setArmature(armature);
         } else {
            Armature armature = (Armature)Armatures.getOrCreate(armatureLocation, humanoid ? HumanoidArmature::new : Armature::new).get();
            ((INpcPatchProvider)provider).setArmature(armature);
         }

         List<Pair<LivingMotion, AnimationAccessor<? extends StaticAnimation>>> defaultAnimations = MobPatchReloadListener.deserializeDefaultAnimations(tag.getCompound("default_livingmotions"));
         npcPatchProvider.setDefaultAnimations(defaultAnimations);
         npcPatchProvider.setFaction((Faction)Faction.ENUM_MANAGER.getOrThrow(tag.getString("faction").toUpperCase(Locale.ROOT)));
         npcPatchProvider.setScale(tag.getCompound("attributes").contains("scale") ? (float)tag.getCompound("attributes").getDouble("scale") : 1.0F);
         if (!clientSide) {
            npcPatchProvider.setStunAnimations(MobPatchReloadListener.deserializeStunAnimations(tag.getCompound("stun_animations")));
            npcPatchProvider.setChasingSpeed(tag.getCompound("attributes").getDouble("chasing_speed"));
            if (humanoid) {
               CustomHumanoidMobPatchProvider humanoidProvider = (CustomHumanoidMobPatchProvider)npcPatchProvider;
               ((ICustomHumanoidMobPatchProvider)humanoidProvider).setHumanoidCombatBehaviors(MobPatchReloadListener.deserializeHumanoidCombatBehaviors(tag.getList("combat_behavior", 10)));
               ((ICustomHumanoidMobPatchProvider)humanoidProvider).setHumanoidWeaponMotions(MobPatchReloadListener.deserializeHumanoidWeaponMotions(tag.getList("humanoid_weapon_motions", 10)));
            } else {
               npcPatchProvider.setCombatBehaviorsBuilder(MobPatchReloadListener.deserializeCombatBehaviorsBuilder(tag.getList("combat_behavior", 10)));
            }
         }

         return (AbstractMobPatchProvider)provider;
      }
   }

   public static Stream<CompoundTag> getDataStream() {
      Stream<CompoundTag> tagStream = TAGMAP.entrySet().stream().map((entry) -> {
         ((CompoundTag)entry.getValue()).putString("id", ((ResourceLocation)entry.getKey()).toString());
         return (CompoundTag)entry.getValue();
      });
      return tagStream;
   }

   @OnlyIn(Dist.CLIENT)
   public static void processServerPacket(SPDatapackSync packet) {
      Armatures.registerEntityTypeArmature(CustomEntities.entityCustomNpc, Armatures.BIPED);
      CompoundTag[] var1 = packet.getTags();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CompoundTag tag = var1[var3];
         boolean disabled = false;
         if (tag.contains("disabled")) {
            disabled = tag.getBoolean("disabled");
         }

         ResourceLocation key = new ResourceLocation(tag.getString("id"));
         AbstractMobPatchProvider provider = null;
         provider = deserializeMobPatchProvider(tag, false);
         branchPatchProvider.addProvider(key, provider);
         AVAILABLE_MODELS.add(key);
         EntityPatchProvider.putCustomEntityPatch(CustomEntities.entityCustomNpc, (entity) -> {
            return () -> {
               return branchPatchProvider.get(entity);
            };
         });
         if (!disabled) {
            if (!tag.contains("preset")) {
               ResourceLocation armatureLocation = new ResourceLocation(tag.getString("armature"));
               armatureLocation = new ResourceLocation(armatureLocation.getNamespace(), "animmodels/" + armatureLocation.getPath() + ".json");
               boolean humanoid = tag.getBoolean("isHumanoid");
               Armature armature = (Armature)Armatures.getOrCreate(armatureLocation, humanoid ? HumanoidArmature::new : Armature::new).get();
               ((INpcPatchProvider)provider).setArmature(armature);
            }

            RenderStorage.registerRenderer(key, tag.contains("preset") ? tag.getString("preset") : tag.getString("renderer"));
         }
      }

   }
}
