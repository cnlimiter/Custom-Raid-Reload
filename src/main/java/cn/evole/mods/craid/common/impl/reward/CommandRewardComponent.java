package cn.evole.mods.craid.common.impl.reward;

import cn.evole.mods.craid.api.IRewardComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CommandRewardComponent implements IRewardComponent {

	public static final String NAME = "cmds";
	private List<CraidCmd> cmds = new ArrayList<>();

	@Override
	public void reward(ServerPlayer player) {
		for (CraidCmd c : cmds) {
			if (!c.global() && !c.cmd().isEmpty()) player.server.getCommands().performPrefixedCommand(new CraidCmdSource(player.getServer()).createCommandSourceStack(), c.cmd());
		}
	}

	@Override
	public void rewardGlobally(Level world) {
		for (CraidCmd c : cmds) {
			if (c.global() && !c.cmd().isEmpty() && world.getServer() != null) world.getServer().getCommands().performPrefixedCommand(new CraidCmdSource(world.getServer()).createCommandSourceStack(), c.cmd());
		}
	}

	@Override
	public void readJson(JsonElement json) {
		final JsonArray array = json.getAsJsonArray();
		if(array != null) {
				for(JsonElement e : array) {
					if(e.isJsonObject()) {
						JsonObject cmd = e.getAsJsonObject();
						cmds.add(new CraidCmd(GsonHelper.getAsString(cmd, "text", ""), GsonHelper.getAsBoolean(cmd, "global", false)));
					}
				}

		}
	}

}
