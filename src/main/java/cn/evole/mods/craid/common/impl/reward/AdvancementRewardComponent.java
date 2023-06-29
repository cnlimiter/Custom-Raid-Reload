package cn.evole.mods.craid.common.impl.reward;

import cn.evole.mods.craid.api.IRewardComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class AdvancementRewardComponent implements IRewardComponent {

	public static final String NAME = "advancements";
	private AdvancementRewards reward = AdvancementRewards.EMPTY;

	@Override
	public void reward(ServerPlayer player) {
		this.reward.grant(player);
	}

	@Override
	public void rewardGlobally(Level world) {
	}

	@Override
	public void readJson(JsonElement json) {
		final JsonObject obj = json.getAsJsonObject();
		if(obj != null) {
			this.reward = AdvancementRewards.deserialize(obj);
		}
	}

}
