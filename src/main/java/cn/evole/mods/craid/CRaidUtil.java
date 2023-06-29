package cn.evole.mods.craid;

import cn.evole.mods.craid.api.IPlacementComponent;
import cn.evole.mods.craid.api.StringUtil;
import cn.evole.mods.craid.common.impl.placement.CenterPlacement;
import cn.evole.mods.craid.common.network.PacketHandler;
import cn.evole.mods.craid.common.network.PlaySoundPacket;
import cn.evole.mods.craid.common.raid.RaidManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
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
			PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PlaySoundPacket(ev.getLocation().toString()));
		}
	}

	public static void sendMsgTo(Player player, Component text) {
		player.sendSystemMessage(text);
	}

	public static boolean isRaidEnable() {
		return CRaidConfig.COMMON_CONFIG.GlobalSettings.EnableRaid.get();
	}

	public static int getRaidWaitTime() {
		return CRaidConfig.COMMON_CONFIG.GlobalSettings.RaidWaitTime.get();
	}

	public static int getRaidRange() {
		return CRaidConfig.COMMON_CONFIG.GlobalSettings.RaidRange.get();
	}

}
