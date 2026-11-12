package com.enderdevelopment.plugin;

import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.Sound;

public enum CarModel {
    SF90_STRADALE("sf90", "Ferrari SF90 Stradale", Material.RED_CONCRETE, 0.42, 0.018, 0.6, Sound.ENTITY_HORSE_GALLOP),
    LAMBORGHINI("lamborghini", "Lamborghini", Material.YELLOW_CONCRETE, 0.46, 0.021, 0.55, Sound.ENTITY_HORSE_GALLOP),
    BUGA_BMW_M4("bmw", "Buga BMW M4", Material.BLUE_CONCRETE, 0.40, 0.017, 0.65, Sound.ENTITY_HORSE_GALLOP),
    AUDI_R8("r8", "Audi R8", Material.GRAY_CONCRETE, 0.44, 0.019, 0.6, Sound.ENTITY_HORSE_GALLOP);

    private final String id;
    private final String displayName;
    private final Material bodyMaterial;
    private final double acceleration;
    private final double fuelUse;
    private final double handling;
    private final Sound engineSound;

    CarModel(String id, String displayName, Material bodyMaterial, double acceleration, double fuelUse, double handling, Sound engineSound) {
        this.id = id;
        this.displayName = displayName;
        this.bodyMaterial = bodyMaterial;
        this.acceleration = acceleration;
        this.fuelUse = fuelUse;
        this.handling = handling;
        this.engineSound = engineSound;
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public Material bodyMaterial() { return bodyMaterial; }
    public double acceleration() { return acceleration; }
    public double fuelUse() { return fuelUse; }
    public double handling() { return handling; }
    public Sound engineSound() { return engineSound; }

    public static CarModel parse(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        for (CarModel model : values()) {
            if (model.id.equals(normalized) || model.name().toLowerCase(Locale.ROOT).equals(normalized)) return model;
        }
        return null;
    }
}
