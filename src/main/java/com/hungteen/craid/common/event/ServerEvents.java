package com.hungteen.craid.common.event;

import com.hungteen.craid.CustomRaid;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

@Mod.EventBusSubscriber(modid = CustomRaid.MOD_ID)
public class ServerEvents {

	@SubscribeEvent
    public static void serverInit(FMLServerStartingEvent ev) {
    }
    
    @SubscribeEvent
    public static void serverShutDown(FMLServerStoppingEvent ev) {
    }
    
}
