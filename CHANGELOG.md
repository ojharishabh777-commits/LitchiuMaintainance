# LitchiuMaintainance 0.1.0-alpha

First public alpha for Minecraft 1.18.2 with Fabric Loader and Java 17.

## Included
- Operator-only `/lmaint status`, `/lmaint players`, `/lmaint save`, and `/lmaint help` commands.
- Rolling 5-second and 1-minute TPS readings, MSPT, player count, uptime, memory, CPU, and free disk metrics.
- Explicit world and player save command.
- Fabric release jar includes the shared `common` module.

## Alpha limitations
- No automated or off-server backups yet.
- No dashboard, log cleanup, alerts, or NeoForge adapter yet.
- CPU and free disk readings may be unavailable on some hosts.
