package net.earthcomputer.clientcommands.command;

import com.demonwav.mcdev.annotations.Translatable;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.earthcomputer.clientcommands.interfaces.IClientSuggestionsProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class ClientCommandHelper {
    /**
     * Personal fork: the server opt-in / operator requirement has been removed. The signature is kept so existing
     * call sites compile unchanged.
     */
    public static void requirePrivileges() throws CommandSyntaxException {
    }

    public static <T> T getFlag(CommandContext<FabricClientCommandSource> ctx, Flag<T> flag) {
        return getFlag(Flag.getActualSource(ctx), flag);
    }

    public static <T> T getFlag(FabricClientCommandSource source, Flag<T> flag) {
        return ((IClientSuggestionsProvider) getSuggestionsProvider(source)).clientcommands_getFlag(flag);
    }

    public static <T> FabricClientCommandSource withFlag(FabricClientCommandSource source, Flag<T> flag, T value) {
        return (FabricClientCommandSource) ((IClientSuggestionsProvider) getSuggestionsProvider(source)).clientcommands_withFlag(flag, value);
    }

    /**
     * Our command source mixins target {@link ClientSuggestionProvider}, which is what Fabric API executes client
     * commands with. Other implementations of the API (e.g. Forgified Fabric API on NeoForge, via Sinytra Connector)
     * may execute them with a different source, in which case we fall back to the connection's suggestions provider.
     */
    public static ClientSuggestionProvider getSuggestionsProvider(FabricClientCommandSource source) {
        if (source instanceof ClientSuggestionProvider suggestionsProvider) {
            return suggestionsProvider;
        }
        return Objects.requireNonNull(source.getClient().getConnection()).getSuggestionsProvider();
    }

    public static void sendError(Component error) {
        sendFeedback(Component.literal("").append(error).withStyle(ChatFormatting.RED));
    }

    public static void sendHelp(Component help) {
        sendFeedback(Component.literal("").append(help).withStyle(ChatFormatting.AQUA));
    }

    public static void sendFeedback(@Translatable String message, Object... args) {
        sendFeedback(Component.translatable(message, args));
    }

    public static void sendFeedback(Component message) {
        Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(message);
    }

    public static void sendRequiresRestart() {
        sendFeedback(Component.translatable("commands.client.requiresRestart").withStyle(ChatFormatting.YELLOW));
    }

    public static void addOverlayMessage(Component message, int time) {
        Gui gui = Minecraft.getInstance().gui;
        gui.hud.setOverlayMessage(message, false);
        gui.hud.overlayMessageTime = time;
    }
}
