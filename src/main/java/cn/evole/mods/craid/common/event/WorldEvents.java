package cn.evole.mods.craid.common.event;

import cn.evole.mods.craid.CRaid;
import cn.evole.mods.craid.common.raid.RaidManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CRaid.MOD_ID)
public class WorldEvents {

	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent ev) {
		if (ev.phase == TickEvent.Phase.END) {
			RaidManager.tickRaids(ev.level);
		}
	}

}
