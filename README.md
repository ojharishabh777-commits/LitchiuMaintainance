# LitchiuMaintainance

A modular Minecraft maintenance toolkit for modded servers, created by **Litchiu**.

## First target
- Minecraft 1.18.2
- Java 17
- Fabric Loader
- Server-first diagnostics, with a client dashboard planned.
- Designed so the shared `common` module can be reused by future Fabric and NeoForge adapters.

## Current alpha features
- `/lmaint status` reports a best-effort snapshot of:
  - Tick-time-derived TPS estimate and latest tick duration (MSPT)
  - JVM used / maximum memory
  - Host CPU load when exposed by the JVM
  - Free disk space at the world save path

> This is an early development scaffold, not a production-ready server manager. TPS is currently an instantaneous estimate based on the latest tick and will be replaced with rolling 5-second / 1-minute windows. CPU and disk metrics depend on host permissions and JVM support.

## Build
Install JDK 17, then run from the project root:

```bash
./gradlew :fabric:build
```

On Windows:

```bat
gradlew.bat :fabric:build
```

The Fabric jar will be in `fabric/build/libs/`.

## Planned modules
1. Monitoring: rolling TPS/MSPT, memory, CPU, disk and player count.
2. Admin dashboard: in-game screen with status cards and safe action confirmations.
3. Backup manager: scheduled backups, retention policy, and restore verification.
4. Log manager: rotation and configurable cleanup.
5. Diagnostics: entity/chunk hotspots and exportable reports.
6. Alerts: Discord webhooks and maintenance notifications.
7. Loader adapters: NeoForge integration after the shared API stabilizes.

## Safety principles
- Never delete world data automatically.
- Never kill entities or unload chunks without an explicit administrator action.
- Backups must be verified before retention cleanup.
- Expensive scans run asynchronously and are rate-limited.
- Features are individually configurable.

## License
MIT. See `LICENSE`.
