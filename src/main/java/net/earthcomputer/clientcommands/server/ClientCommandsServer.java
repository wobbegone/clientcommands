package net.earthcomputer.clientcommands.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ClientCommandsServer implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(OptInPayload.TYPE, OptInPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OptInPayload.TYPE, (_, _) -> {});
    }

    /**
     * Personal fork: the server opt-in / operator requirement has been removed, so the commands that used to require
     * it (/cfindblock, /careastats, /cfind, /cglow, /cgetdata entity, /cghostblock, ...) always work client-side.
     * The signature is kept so existing call sites compile unchanged.
     */
    public static void requirePrivileges() throws CommandSyntaxException {
    }

    private enum OptInPayload implements CustomPacketPayload {
        INSTANCE;

        static final Type<OptInPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("clientcommands", "opt_in"));
        static final StreamCodec<ByteBuf, OptInPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Type<OptInPayload> type() {
            return TYPE;
        }
    }
}
