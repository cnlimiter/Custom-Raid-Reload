package cn.evole.mods.craid.common.impl;

import cn.evole.mods.craid.CRaidUtil;
import cn.evole.mods.craid.api.IPlacementComponent;
import cn.evole.mods.craid.api.ISpawnComponent;
import cn.evole.mods.craid.api.IWaveComponent;
import cn.evole.mods.craid.api.StringUtil;
import cn.evole.mods.craid.common.raid.RaidManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;

public class WaveComponent implements IWaveComponent {

	public static final String NAME = "default";
	private List<ISpawnComponent> spawns = new ArrayList<>();
	private IPlacementComponent placement;
	private int duration;
	private int preCD;

	@Override
	public boolean readJson(JsonObject json) {

		/* duration */
		this.duration = GsonHelper.getAsInt(json, StringUtil.WAVE_DURATION, 0);
		if(this.duration == 0) {
			throw new JsonSyntaxException("Wave duration cannot be empty or zero");
		}

		/* pre tick */
		this.preCD = GsonHelper.getAsInt(json, StringUtil.PRE_CD, 100);

		/* spawn placement */
		this.placement = CRaidUtil.readPlacement(json, false);

		/* spawn list */
		JsonArray jsonSpawns = GsonHelper.getAsJsonArray(json, StringUtil.SPAWNS, new JsonArray());
		for(int i = 0; i < jsonSpawns.size(); ++ i) {
			JsonObject obj = jsonSpawns.get(i).getAsJsonObject();
		    if(obj != null) {
		    	String type = GsonHelper.getAsString(obj, StringUtil.TYPE, "");
	            ISpawnComponent spawn = RaidManager.getSpawnType(type);
	            if(! spawn.readJson(obj)) {
	            	return false;
	            }
			    this.spawns.add(spawn);
		    }
		}
		if(this.spawns.isEmpty()) {
			throw new JsonSyntaxException("Spawn list cannot be empty");
		}

		return true;
	}

	@Override
	public List<ISpawnComponent> getSpawns(){
		return this.spawns;
	}

	@Override
	public IPlacementComponent getPlacement() {
		return this.placement;
	}

	@Override
	public int getPrepareCD() {
		return this.preCD;
	}

	@Override
	public int getLastDuration() {
		return this.duration;
	}

}
