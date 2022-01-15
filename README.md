# Phenyl✨
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/xslingcn/phenyl)](https://github.com/xslingcn/Phenyl/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/xslingcn/phenyl/Build%20Phenyl)](https://github.com/xslingcn/Phenyl/actions)
[![JDK Version](https://img.shields.io/badge/JDK-%3E%3D17-blue)](https://adoptium.net/)
[![GitHub License](https://img.shields.io/github/license/xslingcn/phenyl)](https://github.com/xslingcn/Phenyl/blob/main/LICENSE)

`Phenyl` provides an out-of-the-box bridge between chat apps and Minecraft.

Currently works for [Bungeecord](https://github.com/SpigotMC/BungeeCord)(and its compatible forks, e.g. [Waterfall](https://github.com/PaperMC/Waterfall)) and [Tencent QQ](https://im.qq.com/index).

## Setup
Drop the jar file into your `plugins` folder and modify the `config.yml` in `Phenyl` directory. That's all.

Notice: `Phenyl` requires JDK 17 or above to run. If you're still using older versions, consider updating them.

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

[DreamVoid/MiraiMC](https://github.com/DreamVoid/MiraiMC)
