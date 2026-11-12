package com.enderdevelopment.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class CarCommand implements CommandExecutor, TabCompleter {
    private final VehicleManager vehicles;

    public CarCommand(VehicleManager vehicles) { this.vehicles = vehicles; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Only players can use car commands."); return true; }
        if (!player.hasPermission("endercars.use")) { player.sendMessage("You do not have permission to use cars."); return true; }
        if (args.length == 0) { help(player); return true; }
        try {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "spawn" -> {
                    if (args.length < 2) { player.sendMessage("Choose: sf90, lamborghini, bmw, or r8."); return true; }
                    CarModel model = CarModel.parse(args[1]);
                    if (model == null) { player.sendMessage("Unknown car model."); return true; }
                    vehicles.removeOwned(player);
                    vehicles.spawn(player, model);
                    player.sendMessage("Spawned " + model.displayName() + ". Sprint accelerates, sneak reverses, and your view steers.");
                }
                case "paint" -> {
                    if (args.length < 2) { player.sendMessage("Use a color: red, blue, green, yellow, white, black, or purple."); return true; }
                    vehicles.paint(player, parseColor(args[1]));
                    player.sendMessage("Your car paint was updated.");
                }
                case "refuel" -> { vehicles.refuel(player); player.sendMessage("Fuel tank filled to 100%."); }
                case "fuel" -> {
                    CarData car = vehicles.getOwnedCar(player);
                    player.sendMessage(car == null ? "You do not own a car." : "Fuel: " + Math.round(car.fuel) + "%");
                }
                case "remove" -> { vehicles.removeOwned(player); player.sendMessage("Your car was removed."); }
                default -> help(player);
            }
        } catch (IllegalStateException | IllegalArgumentException exception) {
            player.sendMessage(exception.getMessage());
        }
        return true;
    }

    private Color parseColor(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            case "green" -> Color.GREEN;
            case "yellow" -> Color.YELLOW;
            case "white" -> Color.WHITE;
            case "black" -> Color.BLACK;
            case "purple" -> Color.PURPLE;
            default -> throw new IllegalArgumentException("Unknown paint color.");
        };
    }

    private void help(Player player) {
        player.sendMessage("/car spawn <sf90|lamborghini|bmw|r8>");
        player.sendMessage("/car paint <red|blue|green|yellow|white|black|purple>");
        player.sendMessage("/car refuel, /car fuel, /car remove");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return filter(List.of("spawn", "paint", "refuel", "fuel", "remove"), args[0]);
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) return filter(List.of("sf90", "lamborghini", "bmw", "r8"), args[1]);
        if (args.length == 2 && args[0].equalsIgnoreCase("paint")) return filter(List.of("red", "blue", "green", "yellow", "white", "black", "purple"), args[1]);
        return List.of();
    }

    private List<String> filter(List<String> values, String prefix) {
        return values.stream().filter(value -> value.startsWith(prefix.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
    }
}
