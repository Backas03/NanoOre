package kr.backas.nanoore;

import kr.backas.nanoore.listener.OreBreakListener;
import kr.backas.nanoore.listener.SectionCreateListener;
import kr.backas.nanoore.model.Mine;
import kr.backas.nanoore.model.MineReward;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class NanoOre extends JavaPlugin {

    private static NanoOre instance;

    public static NanoOre getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {
        instance = this;
        registerListener(new OreBreakListener());
        registerListener(new SectionCreateListener());

        getCommand("광산관리").setExecutor(new MineAdminCommand());
        getCommand("광산관리").setPermission("op.op");

        ConfigurationSerialization.registerClass(Mine.class, "Mine");
        ConfigurationSerialization.registerClass(MineReward.class, "MineReward");
    }

    @Override
    public void onDisable() {
        MineManager.getInstance().getSections().forEach(Mine::forceRegenerateAll);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
