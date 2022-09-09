package cn.evolvefield.mods.craid.init.handler;

import cn.evolvefield.mods.craid.common.command.CRaidCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/9/9 19:02
 * Version: 1.0
 */
public class CmdHandler {
    public static void registerCmds(){
        CommandRegistrationCallback.EVENT.register((dispatcher, context, commandSelection) -> {
            CRaidCommand.register(dispatcher);
        });
    }
}
