package cn.evole.mods.craid.common.event;

import cn.evole.mods.craid.CRaid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CRaid.MOD_ID)
public class LivingEvents {

	/**
	 * {@link Raid}
	 */
//	@SubscribeEvent
//	public static void preventRaiderDespawn(LivingSpawnEvent.AllowDespawn ev) {
//		if(ev.getWorld() instanceof ServerWorld && RaidManager.isRaider((ServerWorld) ev.getWorld(), ev.getEntityLiving())) {
//			ev.setResult(Result.DENY);
//		}
//	}

}
