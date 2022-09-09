package cn.evolvefield.mods.craid.common.advancement;

import com.google.gson.JsonElement;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;


public class StringPredicate {

	public static final StringPredicate ANY = new StringPredicate();
	private final String s;

	public StringPredicate() {
		s = "";
	}

	public StringPredicate(String s) {
		this.s = s;
	}

	public boolean test(ServerPlayer player, String ss) {
		if(this == ANY) return true;
		return this.s.equals(ss);
	}

	public static StringPredicate deserialize(@Nullable JsonElement element) {
		if (element != null && element.isJsonPrimitive()) {
			return new StringPredicate(element.getAsString());
		} else {
			return ANY;
		}
	}

	public String serialize() {
		return this.s;
	}

}
