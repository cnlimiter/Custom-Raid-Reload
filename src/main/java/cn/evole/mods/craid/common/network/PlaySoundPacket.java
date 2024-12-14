package cn.evole.mods.craid.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PlaySoundPacket {

	private ResourceLocation type;

	public PlaySoundPacket(ResourceLocation type) {
		this.type = type;
	}

	public PlaySoundPacket(FriendlyByteBuf buffer) {
		this.type = buffer.readResourceLocation();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(type);
	}

	public static class Handler {
		@SuppressWarnings("resource")
		public static void onMessage(PlaySoundPacket message, Supplier<NetworkEvent.Context> ctx) {
		    ctx.get().enqueueWork(()->{
		    	SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(message.type);
		    	if(sound != null) {
		    		Minecraft.getInstance().player.playSound(sound, 1F, 1F);
		    	}
		    });
		    ctx.get().setPacketHandled(true);
	    }
	}
}
