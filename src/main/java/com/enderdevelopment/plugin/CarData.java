package com.enderdevelopment.plugin;

import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.entity.Boat;
import org.bukkit.entity.TextDisplay;

final class CarData {
    final UUID id;
    final Boat boat;
    final CarModel model;
    final TextDisplay display;
    final UUID owner;
    double fuel;
    Color paint;
    double throttle;
    double steering;

    CarData(Boat boat, CarModel model, UUID owner, TextDisplay display, Color paint, double fuel) {
        this.id = boat.getUniqueId();
        this.boat = boat;
        this.model = model;
        this.owner = owner;
        this.display = display;
        this.paint = paint;
        this.fuel = fuel;
    }
}
