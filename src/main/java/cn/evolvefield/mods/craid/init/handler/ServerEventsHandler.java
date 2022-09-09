package cn.evolvefield.mods.craid.init.handler;

import cn.evolvefield.mods.craid.common.raid.RaidManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerEventsHandler {

	public static void onWorldTick() {
		ServerTickEvents.END_WORLD_TICK.register(RaidManager::tickRaids);
	}

}
