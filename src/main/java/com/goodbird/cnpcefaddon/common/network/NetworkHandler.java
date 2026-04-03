package com.goodbird.cnpcefaddon.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;
import noppes.npcs.util.CustomNPCsScheduler;

public class NetworkHandler {
   private static final String PROTOCOL = "CNPCEFADDON";
   public static SimpleChannel CHANNEL;
   public static int index = 0;

   public static void register() {
      CHANNEL = ChannelBuilder.named(new ResourceLocation("cnpcefaddon", "packets")).clientAcceptedVersions("CNPCEFADDON"::equals).serverAcceptedVersions("CNPCEFADDON"::equals).networkProtocolVersion(() -> {
         return "CNPCEFADDON";
      }).simpleChannel();
      index = 0;
      CHANNEL.registerMessage(index++, SPDatapackSync.class, SPDatapackSync::toBytes, SPDatapackSync::fromBytes, SPDatapackSync::handle);
   }

   public static <MSG> void send(ServerPlayer player, MSG msg) {
      CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
         return player;
      }), msg);
   }

   public static <MSG> void sendDelayed(ServerPlayer player, MSG msg, int delay) {
      CustomNPCsScheduler.runTack(() -> {
         CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
            return player;
         }), msg);
      }, delay);
   }

   public static <MSG> void sendNearby(Level level, BlockPos pos, int range, MSG msg) {
      CHANNEL.send(PacketDistributor.NEAR.with(() -> {
         return new TargetPoint((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)range, level.dimension());
      }), msg);
   }

   public static <MSG> void sendNearby(Entity entity, MSG msg) {
      CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> {
         return entity;
      }), msg);
   }

   public static <MSG> void sendAll(MSG msg) {
      CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
   }

   public static <MSG> void sendServer(MSG msg) {
      if (msg instanceof Packet) {
         Minecraft.getInstance().getConnection().getConnection().send((Packet)msg);
      } else {
         CHANNEL.sendToServer(msg);
      }

   }
}
