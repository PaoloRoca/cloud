package ServerPiper;

import io.netty.buffer.ByteBuf;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

/**
 *
 */
public abstract class Packet {

    public static Packet read(ChannelBuffer buffer) throws IOException {
        int id = buffer.readUnsignedShort();

        Packet packet = null;
        packet.get(buffer); // Читаем в пакет данные из буфера
//        ByteBuf byteBuf;
//        byteBuf = buffer;

        return packet;
    }

    public static void write(Packet packet, ChannelBuffer buffer) {
        //buffer.writeChar(packet.getId()); // Отправляем ID пакета
        packet.send(buffer); // Отправляем данные пакета
    }

    // Функции, которые должен реализовать каждый класс пакета
    public abstract void get(ChannelBuffer buffer);
    public abstract void send(ChannelBuffer buffer);
}
