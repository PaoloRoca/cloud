package NettyServer.state_receive;

import NettyServer.FrameHandler;
import io.netty.buffer.ByteBuf;

import java.nio.file.Files;
import java.nio.file.Path;

public class NameStateR implements IStateReceive {
    private FrameHandler handler;

    public NameStateR(FrameHandler handler) {
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
            System.out.println(" NAME: " + new String(fileName));
        }
    }
}
