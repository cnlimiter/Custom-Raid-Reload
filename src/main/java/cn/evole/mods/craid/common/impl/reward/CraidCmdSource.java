package cn.evole.mods.craid.common.impl.reward;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @Name: CraidCmdSource
 * @Author: cnlimiter
 * @CreateTime: 2024/12/13 14:26
 * @Description:
 **/
public class CraidCmdSource implements CommandSource {
    private static final Component RCON_COMPONENT = Component.literal("CRaid");
    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;
    public CraidCmdSource(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    public void prepareForCommand() {
        this.buffer.setLength(0);
    }

    public String getCommandResponse() {
        return this.buffer.toString();
    }

    public CommandSourceStack createCommandSourceStack() {
        ServerLevel serverlevel = this.server.overworld();
        return new CommandSourceStack(this, Vec3.atLowerCornerOf(serverlevel.getSharedSpawnPos()), Vec2.ZERO, serverlevel, 4, "CRaid", RCON_COMPONENT, this.server, (Entity)null);
    }

    public void sendSystemMessage(Component arg) {
        this.buffer.append(arg.getString()).append("\n");
    }

    public boolean acceptsSuccess() {
        return true;
    }

    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }
}
