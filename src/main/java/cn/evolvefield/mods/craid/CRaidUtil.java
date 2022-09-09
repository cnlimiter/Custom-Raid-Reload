package cn.evolvefield.mods.craid;

import cn.evolvefield.mods.craid.api.IPlacementComponent;
import cn.evolvefield.mods.craid.api.StringUtil;
import cn.evolvefield.mods.craid.common.impl.placement.CenterPlacement;
import cn.evolvefield.mods.craid.common.raid.RaidManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map.Entry;

public class CRaidUtil {

	/**
	 * flag set false to make default placement as null.
	 */
	@Nullable
	public static IPlacementComponent readPlacement(JsonObject jsonObject, boolean flag) {
		/* spawn placement */
		IPlacementComponent placement = flag ? new CenterPlacement() : null;
		JsonObject obj = GsonHelper.getAsJsonObject(jsonObject, StringUtil.SPAWN_PLACEMENT, null);
	    if(obj != null && ! obj.entrySet().isEmpty()) {
	       for(Entry<String, JsonElement> entry : obj.entrySet()) {
	  		    final IPlacementComponent tmp = RaidManager.getSpawnPlacement(entry.getKey());
	    	    if(tmp != null) {
	    		    tmp.readJson(entry.getValue());
	    		    placement = tmp;
	    	    } else {
	    		    CRaid.LOGGER.warn("Placement Component : Read Spawn Placement Wrongly");
	    	    }
	   		    break;
	   	    }
	    }
	    return placement;
	}

	public static ResourceLocation prefix(String s) {
		return new ResourceLocation(CRaid.MOD_ID, s);
	}

	/**
	 * gen random from min to max.
	 */
	public static int getRandomMinMax(RandomSource rand, int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}


	public static int getRandomInRange(RandomSource rand, int range) {
		return rand.nextInt(range << 1 | 1) - range;
	}

	public static void playClientSound(Player player, SoundEvent ev) {
		if(ev != null) {
			FriendlyByteBuf buf = PacketByteBufs.create();
			buf.writeResourceLocation(ev.getLocation());
			ServerPlayNetworking.send((ServerPlayer) player, new ResourceLocation(CRaid.MOD_ID, "main"), buf);
		}
	}

	public static void sendMsgTo(Player player, Component text) {
		player.sendSystemMessage(text);
	}

	public static boolean isRaidEnable() {
		return CRaid.configHandler.getConfig().isEnableRaid();
	}

	public static int getRaidWaitTime() {
		return CRaid.configHandler.getConfig().getRaidWaitTime();
	}

	public static int getRaidRange() {
		return CRaid.configHandler.getConfig().getRaidRange();
	}

}
