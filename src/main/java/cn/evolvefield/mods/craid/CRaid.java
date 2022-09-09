package cn.evolvefield.mods.craid;

import cn.evolvefield.mods.craid.api.CRaidAPI;
import cn.evolvefield.mods.craid.common.advancement.AdvancementHandler;
import cn.evolvefield.mods.craid.common.impl.RaidComponent;
import cn.evolvefield.mods.craid.common.impl.SpawnComponent;
import cn.evolvefield.mods.craid.common.impl.WaveComponent;
import cn.evolvefield.mods.craid.common.impl.amount.ConstantAmount;
import cn.evolvefield.mods.craid.common.impl.amount.RandomAmount;
import cn.evolvefield.mods.craid.common.impl.placement.CenterPlacement;
import cn.evolvefield.mods.craid.common.impl.placement.OffsetPlacement;
import cn.evolvefield.mods.craid.common.impl.placement.OuterPlacement;
import cn.evolvefield.mods.craid.common.impl.reward.AdvancementRewardComponent;
import cn.evolvefield.mods.craid.init.handler.*;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CRaid implements ModInitializer
{
	public static final String MOD_ID = "craid";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static ConfigHandler configHandler;

	@Override
	public void onInitialize() {
		configHandler = new ConfigHandler();
		AdvancementHandler.init();
		ServerEventsHandler.onWorldTick();
		CmdHandler.registerCmds();
		NetWorkHandler.onRun();
		ResourceHandler.addReloadListenerEvent();
		registerMisc();

	}

    public static void registerMisc() {
    	final CRaidAPI.ICustomRaidAPI api = CRaidAPI.get();

    	api.registerRaidType(RaidComponent.NAME, RaidComponent.class);

    	api.registerWaveType(WaveComponent.NAME, WaveComponent.class);

    	api.registerSpawnType(SpawnComponent.NAME, SpawnComponent.class);

    	api.registerSpawnAmount(ConstantAmount.NAME, ConstantAmount.class);
    	api.registerSpawnAmount(RandomAmount.NAME, RandomAmount.class);

    	api.registerSpawnPlacement(CenterPlacement.NAME, CenterPlacement.class);
    	api.registerSpawnPlacement(OuterPlacement.NAME, OuterPlacement.class);
    	api.registerSpawnPlacement(OffsetPlacement.NAME, OffsetPlacement.class);

    	api.registerReward(AdvancementRewardComponent.NAME, AdvancementRewardComponent.class);
    }






}
