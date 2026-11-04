package dev.litchiu.maintainance.fabric;

import dev.litchiu.maintainance.core.ServerMetrics;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class LitchiuMaintainance implements ModInitializer {
    public static final String MOD_ID = "litchiumaintenance";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private long lastTick = System.nanoTime();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onTick);
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            dispatcher.register(literal("lmaint")
                .then(literal("status").executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    long usable = ServerMetrics.getUsableDiskMb(server.getSavePath(WorldSavePath.ROOT));
                    ctx.getSource().sendFeedback(new LiteralText(
                        "§6[LitchiuMaintainance] §aTPS: " + String.format("%.1f", ServerMetrics.getTps())
                        + " | MSPT: " + String.format("%.2f", ServerMetrics.getMspt())
                        + " | RAM: " + ServerMetrics.getUsedMemoryMb() + "/" + ServerMetrics.getMaxMemoryMb()
                        + " MB | CPU: " + cpuText()
                        + " | Free disk: " + (usable < 0 ? "unavailable" : usable + " MB")), false);
                    return 1;
                }))
            )
        );
        LOGGER.info("LitchiuMaintainance {} initialized. Use /lmaint status.", "0.1.0-alpha");
    }

    private void onTick(MinecraftServer server) {
        long now = System.nanoTime();
        ServerMetrics.recordTick(now - lastTick);
        lastTick = now;
    }

    private static String cpuText() {
        double cpu = ServerMetrics.getCpuLoadPercent();
        return cpu < 0 ? "unavailable" : String.format("%.1f%%", cpu);
    }
}
