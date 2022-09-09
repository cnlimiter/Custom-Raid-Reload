package cn.evolvefield.mods.craid.init.handler;

import cn.evolvefield.mods.craid.CRaid;
import cn.evolvefield.mods.craid.init.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/27 22:15
 * Version: 1.0
 */
public class ConfigHandler {
    private Config config;
    public Config getConfig() {
        return config;
    }
    private final File file;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public Gson GSON;

    public ConfigHandler(){
        this.file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CRaid.MOD_ID + ".json");
        this.GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        load(false);
    }


    public void load(boolean async) {
        Runnable task = () -> {
            try {
                //read if exists
                if (file.exists()) {
                    String fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
                    config = GSON.fromJson(fileContents, Config.class);

                } else { //write new if no config file exists
                    writeNewConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
                writeNewConfig();
            }
        };

        if (async) executor.execute(task);
        else task.run();
    }
    public void writeNewConfig() {
        config = new Config();
        save(false);
    }
    public void save(boolean async) {
        Runnable task = () -> {
            try {
                if (config != null) {
                    String serialized = GSON.toJson(config);
                    FileUtils.writeStringToFile(file, serialized, Charset.defaultCharset());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (async) executor.execute(task);
        else task.run();
    }

}
