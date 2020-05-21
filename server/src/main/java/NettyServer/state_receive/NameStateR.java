package NettyServer.state_receive;

import io.netty.buffer.ByteBuf;

import java.nio.file.Files;
import java.nio.file.Path;

public class NameStateR implements IStateReceive {
    private FrameHandlerOop handler;

    public NameStateR(FrameHandlerOop handler) {
        this.handler = handler;
    }

    @Override
    public void name(ByteBuf in) throws Exception {
//        int bytesRead = in.readableBytes(); //сколько байт доступно для чтения
        if (in.readableBytes() >= handler.getNameLength()) {
            byte[] fileName = new byte[handler.getNameLength()];
            in.readBytes(fileName);
            Path path = handler.getConsumer().getUserDirectory().resolve(new String(fileName));
            handler.setOut(Files.newOutputStream(path));
            handler.setState(handler.getFileNameStateR());
            System.out.print(" NAME: " + new String(fileName));
        }
    }
}
