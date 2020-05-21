package NettyServer.state_receive;

import NettyServer.FrameHandler;
import io.netty.buffer.ByteBuf;

public class NameLengthStateR implements IStateReceive {
    private FrameHandler handler;

    public NameLengthStateR(FrameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void nameLength(ByteBuf in) {
        if (in.readableBytes() >= 1) {
//            int bytesRead = in.readableBytes(); //сколько байт доступно для чтения
//            handler.setNameLength(in.getByte(1));

            handler.setNameLength(in.readByte());
            handler.setState(handler.getNameStateR());
            System.out.println(" NAME_LENGTH: " + in.getByte(1));
        }
    }
}
