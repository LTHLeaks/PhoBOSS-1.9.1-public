package me.earth.phobos;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.phobos.features.modules.misc.RPC;

public class Discord {

    public static DiscordRichPresence presence;
    private static final DiscordRPC rpc;
    private static RPC discordrpc;
    private static Thread thread;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("980891030956171264", handlers, true, "");
        Discord.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        Discord.presence.details = Phobos.getName() + " v" + "1.9.1";
        Discord.presence.state = "PhoBOSS 1.9.1";
        Discord.presence.largeImageKey = "image_2022-05-30_125303789";
        Discord.presence.largeImageText = "PhoBOSS 1.9.1 rewritten by SomeSadKid_";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                Discord.presence.details = Phobos.getName() + " v" + "1.9.1";
                Discord.presence.state = "SomeSadKid Owns You";
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    static {
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}
