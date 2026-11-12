package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class VehicleManager implements Listener {
    private static final double MAX_FUEL = 100.0;
    private final JavaPlugin plugin;
    private final Map<UUID, CarData> cars = new HashMap<>();
    private final NamespacedKey carKey;

    public VehicleManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.carKey = new NamespacedKey(plugin, "car_model");
    }

    public CarData spawn(Player owner, CarModel model) {
        Location location = owner.getLocation().clone().add(owner.getLocation().getDirection().setY(0).normalize().multiply(2));
        Boat boat = (Boat) owner.getWorld().spawnEntity(location, EntityType.OAK_BOAT);
        boat.setInvulnerable(false);
        boat.setRotation(owner.getLocation().getYaw(), 0);
        boat.getPersistentDataContainer().set(carKey, PersistentDataType.STRING, model.id());
        TextDisplay display = (TextDisplay) owner.getWorld().spawnEntity(location.clone().add(0, 1.05, 0), EntityType.TEXT_DISPLAY);
        display.text(Component.text(model.displayName()).color(NamedTextColor.WHITE));
        display.setBillboard(TextDisplay.Billboard.CENTER);
        display.setSeeThrough(false);
        CarData data = new CarData(boat, model, owner.getUniqueId(), display, defaultPaint(model), MAX_FUEL);
        cars.put(boat.getUniqueId(), data);
        boat.addPassenger(owner);
        refreshDisplay(data);
        return data;
    }

    public void startPhysics() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    private void tick() {
        for (CarData car : cars.values().toArray(CarData[]::new)) {
            if (!car.boat.isValid()) { remove(car); continue; }
            Entity driver = car.boat.getPassengers().isEmpty() ? null : car.boat.getPassengers().get(0);
            if (!(driver instanceof Player player)) {
                car.boat.setVelocity(car.boat.getVelocity().multiply(0.82));
                continue;
            }
            double throttle = player.isSprinting() ? 1.0 : (player.isSneaking() ? -0.55 : 0.15);
            if (car.fuel <= 0.0) throttle = 0.0;
            car.throttle = throttle;
            car.steering = normalizeAngle(player.getLocation().getYaw() - car.boat.getYaw());
            float yaw = car.boat.getYaw() + (float) Math.max(-7.0, Math.min(7.0, car.steering * 0.08 * car.model.handling()));
            car.boat.setRotation(yaw, 0);
            Vector forward = new Vector(-Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
            Vector velocity = car.boat.getVelocity().multiply(0.90).add(forward.multiply(car.model.acceleration() * throttle));
            velocity.setY(Math.max(-0.35, Math.min(0.35, velocity.getY())));
            car.boat.setVelocity(velocity);
            if (Math.abs(throttle) > 0.01) {
                car.fuel = Math.max(0.0, car.fuel - car.model.fuelUse() * Math.abs(throttle));
                player.getWorld().playSound(car.boat.getLocation(), car.model.engineSound(), SoundCategory.VEHICLE, 0.45f, 0.7f + (float) Math.min(1.0, velocity.length()));
            }
            car.display.teleport(car.boat.getLocation().clone().add(0, 1.05, 0));
            refreshDisplay(car);
        }
    }

    private float normalizeAngle(float angle) {
        angle %= 360.0f;
        if (angle > 180.0f) angle -= 360.0f;
        if (angle < -180.0f) angle += 360.0f;
        return angle;
    }

    public CarData getOwnedCar(Player player) {
        for (CarData car : cars.values()) if (car.owner.equals(player.getUniqueId())) return car;
        return null;
    }

    public void paint(Player player, Color color) {
        CarData car = getOwnedCar(player);
        if (car == null) throw new IllegalStateException("You do not own a car.");
        car.paint = color;
        refreshDisplay(car);
    }

    public void refuel(Player player) {
        CarData car = getOwnedCar(player);
        if (car == null) throw new IllegalStateException("You do not own a car.");
        car.fuel = MAX_FUEL;
    }

    public void removeOwned(Player player) {
        CarData car = getOwnedCar(player);
        if (car != null) remove(car);
    }

    private void refreshDisplay(CarData car) {
        int percent = (int) Math.round(car.fuel);
        car.display.text(Component.text(car.model.displayName() + "  |  Fuel: " + percent + "%").color(NamedTextColor.WHITE));
        car.display.setGlowColor(car.paint);
    }

    private Color defaultPaint(CarModel model) {
        return switch (model) {
            case SF90_STRADALE -> Color.RED;
            case LAMBORGHINI -> Color.YELLOW;
            case BUGA_BMW_M4 -> Color.BLUE;
            case AUDI_R8 -> Color.GRAY;
        };
    }

    private void remove(CarData car) {
        cars.remove(car.id);
        car.display.remove();
        car.boat.remove();
    }

    public void removeAll() {
        for (CarData car : cars.values().toArray(CarData[]::new)) remove(car);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        CarData car = cars.get(event.getEntity().getUniqueId());
        if (car != null) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { removeOwned(event.getPlayer()); }
}
