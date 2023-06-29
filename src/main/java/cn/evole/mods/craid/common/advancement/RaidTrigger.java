package cn.evole.mods.craid.common.advancement;

import cn.evole.mods.craid.CRaidUtil;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class RaidTrigger extends SimpleCriterionTrigger<RaidTrigger.Instance> {

	private static final ResourceLocation ID = CRaidUtil.prefix("raid");
	public static final RaidTrigger INSTANCE = new RaidTrigger();

	public ResourceLocation getId() {
		return ID;
	}


	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */

	public void trigger(ServerPlayer player, String s) {
		this.trigger(player, (instance) -> {
			return instance.test(player, s);
		});
	}


	@Override
	protected @NotNull Instance createInstance(JsonObject jsonObject, @NotNull ContextAwarePredicate arg, @NotNull DeserializationContext arg2) {
		StringPredicate amount = StringPredicate.deserialize(jsonObject.get("type"));
		return new Instance(arg, amount);
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final StringPredicate type;

		public Instance(ContextAwarePredicate player, StringPredicate res) {
			super(ID, player);
			this.type = res;
		}

		public boolean test(ServerPlayer player, String s) {
			return this.type.test(player, s);
		}


		@Override
		public @NotNull JsonObject serializeToJson(@NotNull SerializationContext pContext) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("type", this.type.serialize());
			return jsonobject;
		}
	}

}
