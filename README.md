# Minecraft Paper Driving Car Plugin

VroomBedrock is a Minecraft Paper plugin that adds drivable sports car entities with vehicle physics, fuel management, paint color customization, engine sounds, passenger support through boat entities, and four selectable car models: Ferrari SF90 Stradale, Lamborghini, Buga BMW M4, and Audi R8.

## Install

1. Install Java 21.
2. Run the build from the project directory:

   ```bash
   mvn clean package
   ```

3. Copy the generated `target/plugin.jar` file into the server's `plugins` directory.
4. Start or restart a Paper 1.21 server.
5. Make sure players have the `endercars.use` permission. This permission is enabled by default.
6. Check the server log for the plugin enable message.

The plugin is built for Paper and does not contain Minecraft Bedrock behavior-pack or resource-pack files.

## Use

Use the `/car` command in game.

```text
/car spawn <sf90|lamborghini|bmw|r8>
```

Spawns a car and places the player in it.

```text
/car paint <red|blue|green|yellow|white|black|purple>
```

Changes the car's paint color indicator.

```text
/car refuel
```

Refills the car's fuel to 100 percent.

```text
/car fuel
```

Displays the current fuel level.

```text
/car remove
```

Removes the player's car.

While driving:

- Sprint to accelerate.
- Sneak to reverse.
- Use the player's view direction to steer.
- The car slows down when no driver is present.
- Fuel decreases while accelerating or reversing.
- The vehicle display shows the model name and current fuel percentage.
- A car can use the passenger system provided by its underlying boat entity.

The permission required for all car commands is `endercars.use`.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/e0a0db4d-1911-41fb-9a44-dfba373a172d?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=e0a0db4d-1911-41fb-9a44-dfba373a172d
