package net.agusdropout.bloodyhell.networking.packet;

import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CrimsonVeilC2SPacket {

   public CrimsonVeilC2SPacket(){

   }
   public CrimsonVeilC2SPacket(FriendlyByteBuf buf) {

   }
   public void toBytes(FriendlyByteBuf Buf){

   }
   public boolean handle(Supplier<NetworkEvent.Context> supplier){
       NetworkEvent.Context context = supplier.get();
       context.enqueueWork(() -> {
           //HERE WE ARE ON THE SERVER!
           ServerPlayer player = context.getSender();
           ServerLevel level = player.serverLevel();

               player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {
                   crimsonVeil.addCrimsomveil(1);
                   ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), player);
               });

               player.getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(crimsonVeil -> {

                   ModMessages.sendToPlayer(new CrimsonVeilDataSyncS2CPacket(crimsonVeil.getCrimsonVeil()), player);
               });



       });
       return true;
}

}

