package cn.evole.mods.craid.common.event;

import cn.evole.mods.craid.CRaid;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CRaid.MOD_ID)
public class ServerEvents {

	@SubscribeEvent
    public static void serverInit(ServerStartingEvent ev) {
    }

    @SubscribeEvent
    public static void serverShutDown(ServerStoppingEvent ev) {
    }

}
