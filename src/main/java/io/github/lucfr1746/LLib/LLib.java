package io.github.lucfr1746.LLib;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class LLib extends JavaPlugin {

    private static LLib plugin;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getInstance() {
        return plugin;
    }
}
