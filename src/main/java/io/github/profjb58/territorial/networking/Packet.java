package io.github.profjb58.territorial.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface Packet {

    void write(PacketByteBuf buf);

    void read(PacketByteBuf buf);

    void send();

    Identifier getId();
}
