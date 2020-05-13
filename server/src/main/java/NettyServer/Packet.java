package NettyServer;

import io.netty.buffer.ByteBuf;
import org.jboss.netty.buffer.ChannelBuffer;

public abstract class Packet {
    ByteBuf msg;
    Consumer consumer;

    public void init(Consumer consumer, ByteBuf msg) {
        this.consumer = consumer;
        this.msg = msg;
    }

    public static void write(Packet packet, ChannelBuffer buffer) {
        //buffer.writeChar(packet.getId()); // Отправляем ID пакета
        //packet.send(buffer); // Отправляем данные пакета
    }

    // Функции, которые должен реализовать каждый класс пакета
    public abstract void get();
    public abstract void send();
}
