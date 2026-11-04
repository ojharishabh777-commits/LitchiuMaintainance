package dev.litchiu.maintainance.fabric;

import dev.litchiu.maintainance.core.ServerMetrics;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.literal;

public class LitchiuMaintainance implements ModInitializer {
    public static final String MOD_ID = "litchiumaintenance";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private long lastTickNanos;

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(server -> lastTickNanos = System.nanoTime());
        ServerTickEvents.END_SERVER_TICK.register(this::onTick);
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            dispatcher.register(literal("lmaint")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("status").executes(ctx -> showStatus(ctx.getSource().getServer(), ctx.getSource())))
                .then(literal("players").executes(ctx -> showPlayers(ctx.getSource().getServer(), ctx.getSource())))
                .then(literal("save").executes(ctx -> save(ctx.getSource().getServer(), ctx.getSource())))
                .then(literal("help").executes(ctx -> {
                    ctx.getSource().sendFeedback(new LiteralText(
                        "§6LitchiuMaintainance commands: /lmaint status, /lmaint players, /lmaint save"), false);
                    return 1;
                }))
            )
        );
        LOGGER.info("LitchiuMaintainance initialized. Use /lmaint help.");
    }

    private int showStatus(MinecraftServer server, net.minecraft.server.command.ServerCommandSource source) {
        long freeDiskMb = ServerMetrics.getUsableDiskMb(server.getSavePath(WorldSavePath.ROOT));
        long uptimeSeconds = Math.max(0L, server.getTicks() / 20L);
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        source.sendFeedback(new LiteralText(
            "§6[LitchiuMaintainance] §aTPS: " + String.format("%.1f", ServerMetrics.getTps())
            + " (5s) / " + String.format("%.1f", ServerMetrics.getTps1m()) + " (1m)"
            + " | MSPT: " + String.format("%.2f", ServerMetrics.getMspt()) + " latest / "
            + String.format("%.2f", ServerMetrics.getMspt1m()) + " 1m"
            + " | Players: " + server.getPlayerManager().getCurrentPlayerCount() + "/"
            + server.getPlayerManager().getMaxPlayerCount()
            + " | RAM: " + ServerMetrics.getUsedMemoryMb() + "/" + ServerMetrics.getMaxMemoryMb() + " MB"
            + " | CPU: " + cpuText()
            + " | Free disk: " + (freeDiskMb < 0 ? "unavailable" : freeDiskMb + " MB")
            + " | Uptime: " + String.format("%02d:%02d:%02d", hours, minutes, seconds)), false);
        return 1;
    }

    private int showPlayers(MinecraftServer server, net.minecraft.server.command.ServerCommandSource source) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        if (players.isEmpty()) {
            source.sendFeedback(new LiteralText("§eNo players are online."), false);
            return 1;
        }
        String names = players.stream().map(player -> player.getGameProfile().getName()).collect(Collectors.joining("§7, §f"));
        source.sendFeedback(new LiteralText("§6Online players (" + players.size() + "): §f" + names), false);
        return players.size();
    }

    private int save(MinecraftServer server, net.minecraft.server.command.ServerCommandSource source) {
        boolean success = server.saveAll(false, true, true);
        source.sendFeedback(new LiteralText(success
            ? "§aWorld and player data saved."
            : "§cThe server reported that the save did not complete successfully."), false);
        return success ? 1 : 0;
    }

    private void onTick(MinecraftServer server) {
        long now = System.nanoTime();
        if (lastTickNanos != 0L) ServerMetrics.recordTick(now - lastTickNanos);
        lastTickNanos = now;
    }

    private static String cpuText() {
        double cpu = ServerMetrics.getCpuLoadPercent();
        return cpu < 0 ? "unavailable" : String.format("%.1f%%", cpu);
    }
}
