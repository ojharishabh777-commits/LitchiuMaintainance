# LitchiuMaintainance

A modular Minecraft maintenance toolkit for modded servers, created by **Litchiu**.

## First target
- Minecraft 1.18.2
- Java 17
- Fabric Loader
- Server-first diagnostics, with a client dashboard planned.
- Designed so the shared `common` module can be reused by future Fabric and NeoForge adapters.

## Current alpha features
All `/lmaint` commands require operator permission level 2 or higher. The console can also use them.

- `/lmaint status` shows rolling 5-second and 1-minute TPS, latest and 1-minute average MSPT, player count, uptime, JVM memory, best-effort host CPU load, and free space at the world save path.
- `/lmaint players` lists online players.
- `/lmaint save` flushes world and player data to disk and reports whether the save succeeded.
- `/lmaint help` lists the available commands.

CPU and disk metrics depend on host permissions and JVM support. This is still an early alpha: it does not yet include automated backups, log cleanup, alerts, or a dashboard.

## Install
1. Install Fabric Loader for Minecraft 1.18.2 on the server.
2. Install Fabric API for Minecraft 1.18.2.
3. Put the `LitchiuMaintainance-Fabric-*.jar` release file in the server's `mods` folder.
4. Start the server. Operators can run `/lmaint help` in game or from the server console.

## Build
Install JDK 17 and Gradle, then run from the project root (Windows: `gradle.bat :fabric:build`):

```bash
gradle :fabric:build
```

The Fabric jar will be in `fabric/build/libs/`.

## Planned modules
1. Admin dashboard: in-game screen with status cards and safe action confirmations.
2. Backup manager: scheduled backups, retention policy, and restore verification.
3. Log manager: rotation and configurable cleanup.
4. Diagnostics: entity/chunk hotspots and exportable reports.
5. Alerts: Discord webhooks and maintenance notifications.
6. Loader adapters: NeoForge integration after the shared API stabilizes.

## Safety principles
- Never delete world data automatically.
- Never kill entities or unload chunks without an explicit administrator action.
- Backups must be verified before retention cleanup.
- Expensive scans run asynchronously and are rate-limited.
- Features are individually configurable.

## License
MIT. See `LICENSE`.
