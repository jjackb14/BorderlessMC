# BorderlessMC

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen?style=flat-square)](https://github.com/jjackb14/BorderlessMC/releases)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)](https://adoptium.net/)

Borderless fullscreen for Minecraft (Fabric) — runs fullscreen as a borderless window on the monitor the game is currently on.

## Features
- F11 toggles borderless fullscreen on/off
- Restores previous window size and position on exit
- Multi-monitor aware (uses current monitor)
- Toggle mod enabled/disabled with a keybind
- Shows status on the F3 debug screen

## Compatibility
- Minecraft: 1.21.11
- Mod Loader: Fabric
- Requires: Fabric API

## Controls
- **F11**: Toggle borderless fullscreen
- **B** (default): Toggle BorderlessMC enabled/disabled (rebindable in Controls)

## Config
Stored at:
- `.minecraft/config/borderlessmc.json`

```json
{ "enabled": true }
