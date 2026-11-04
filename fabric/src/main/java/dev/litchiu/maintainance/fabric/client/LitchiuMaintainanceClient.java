package dev.litchiu.maintainance.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/** Client-side entrypoint reserved for the admin dashboard and client diagnostics. */
public class LitchiuMaintainanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Dashboard screen is added in the next implementation milestone.
    }
}
