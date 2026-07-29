# SnowFlake [ 1.21.x ]
A Minecraft Farland modern generation plugin.

<img width="2560" height="1351" alt="2026-07-27_13 22 14" src="https://github.com/user-attachments/assets/03c06eb3-127f-4369-b6f3-f9766d52dde1" />

---
# What is SnowFlake?
This is a simple minecraft Paper Plugin for FarLand generation in 1.21.x.
Plugin modificating generation in worlds which typed in config (generation.yml)
in custom distance (generation.yml). Brings a old minecraft bug of generation
in new minecraft versions.

Plugins generates farlands in world in realtime with BlockPopulator.

---
# Configs
| File | What doing |
|-------------|-------------|
| `config.yml`| main plugin config with command settings    |
| `generation.yml` | plugin generation config    |


+ generation.yml
```yml
version: 1.2
# The worlds where Far Lands generation is applied.
worlds:
- world

#  Far Lands generation settings.
farlands:
  # Distance from world center where Far Lands begin. Original Beta 1.7.3 value: 12550821.
  distance: 12550821
  # Noise coordinate scale.
  coordinate-scale: 684.412
  # Noise height scale.
  height-scale: 684.412
  # Min noise height.
  min-height-y: 0
  # Max noise height.
  max-height-y: 128
  
#  Void fade — gradual corruption towards the void.
  void:
    # Distance at which terrain starts to corrupt.
    start-distance: 12000
    # Distance at which terrain disappears completely.
    end-distance: 14000000
```

+ config.yml
```yml
# Don't change it if you don't know what it is.
version: 1.2

#  prefix for messages in minecraft.
prefix: <gradient:#4490f3:#b3d4ff>SnowFlake</gradient>
message_templates:
  multiline:
  - <white>❏ <bold>%prefix%</white>  <bold>%module%</bold>
  - <bold><#7fc4f5>*</#7fc4f5></bold> <white>%message%</white>

#  neofetch for plugin fetch on start.
neofetch:
- <blue>                           __ _       _           </blue>
- <blue>                          / _| |     | |          </blue>
- <blue>  ___ _ __   _____      _| |_| | __ _| | _____    </blue>
- <blue> / __| '_ \ / _ \ \ /\ / /  _| |/ _` | |/ / _ \ </blue>
- <blue> \__ \ | | | (_) \ V  V /| | | | (_| |   <  __/ </blue>
- <blue> |___/_| |_|\___/ \_/\_/ |_| |_|\__,_|_|\_\___| </blue>

#  Command in plugin with permissions.
commands:
  command: snowflake
  aliases:
  - sf
  permissions:
    config: snowflake.config
    reload: snowflake.reload
    dev_perm: snowflake.dev

#  Plugin command messages.
messages:
  help_message:
  - '<white>Использование: /sf {argument}</white>'
  - <#7fc4f5>Подкоманды:</#7fc4f5>
  - <white>/sf reload <gray>-</gray> Перезагрузка конфигов.</white>
  - <white>/sf config <gray>-</gray> Вывод настроек генерации.</white>
  reload_message: <white>Config has reloaded successfully!</white>
  config_message:
  - '<white>Active worlds: <#7fc4f5>%worlds%</#7fc4f5><white>'
  - '<white>Distance: <#7fc4f5>%distance%</#7fc4f5><white>'
  - '<white>CoordinateScale: <#7fc4f5>%coordinateScale%</#7fc4f5><white>'
  - '<white>HeightScale: <#7fc4f5>%heightScale%</#7fc4f5><white>'
  - '<white>MinHeightY: <#7fc4f5>%minHeightY%</#7fc4f5></white>'
  - '<white>MaxHeightY: <#7fc4f5>%maxHeightY%</#7fc4f5></white>'
```

Using libs:
- [kaml](https://github.com/charleskorn/kaml)
- [cloud](https://github.com/Incendo/cloud)

---
How to build?
1. Open source project in Intellej Idea
2. Open console
3. type `./gradlew build`
Done: .jar builded in root path project `build/libs`

or:
1. Open gradle menu:
2. Start `SnowFlake\Task\build\build`
Done: .jar builded in root path project `build/libs`

