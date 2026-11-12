package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private VehicleManager vehicleManager;

    @Override
    public void onEnable() {
        vehicleManager = new VehicleManager(this);
        Bukkit.getPluginManager().registerEvents(vehicleManager, this);
        CarCommand command = new CarCommand(vehicleManager);
        getCommand("car").setExecutor(command);
        getCommand("car").setTabCompleter(command);
        vehicleManager.startPhysics();
        getLogger().info("EnderCars enabled: SF90 Stradale, Lamborghini, Buga BMW M4, and Audi R8");
    }

    @Override
    public void onDisable() {
        if (vehicleManager != null) {
            vehicleManager.removeAll();
        }
    }
}
