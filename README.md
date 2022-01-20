<div align="center">
   <img width="160" src="https://s3.bmp.ovh/imgs/2022/01/665fd8e6ead3d2c8.png" alt="logo"></br></br>

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/turnalive/phenyl)](https://github.com/TurnALive/Phenyl/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/turnalive/phenyl/Build%20Phenyl)](https://github.com/TurnALive/Phenyl/actions)
[![JDK Version](https://img.shields.io/badge/JDK-%3E%3D17-blue)](https://adoptium.net/)
[![GitHub License](https://img.shields.io/github/license/turnalive/phenyl)](https://github.com/TurnALive/Phenyl/blob/main/LICENSE)
</div>

# Phenyl✨
`Phenyl` provides an out-of-the-box bridge between chat apps and Minecraft.

Currently works for [Bungeecord](https://github.com/SpigotMC/BungeeCord)(and its compatible forks,
e.g. [Waterfall](https://github.com/PaperMC/Waterfall)) and [Tencent QQ](https://im.qq.com/index).

## Setup

Drop the jar file into your `plugins` folder and modify the `config.yml` in `Phenyl` directory. That's all.

Notice: `Phenyl` requires JRE 17 or higher to run. If you're still using an old version, consider updating it.

## Build

To compile `Phenyl` from source, make sure you're using JDK17 and run:

```bash
./gradlew jar
```

The generated jar file should be found in the project root's `build/libs` directory.

## Contribute

`Phenyl` is an open source project, feel free to open pull requests.

## Credits

[mamoe/mirai](https://github.com/mamoe/mirai)

[SpigotMC/BungeeCord](https://github.com/SpigotMC/BungeeCord)

[LuckPerms/LuckPerms](https://github.com/LuckPerms/LuckPerms)

[DreamVoid/MiraiMC](https://github.com/DreamVoid/MiraiMC)
