package NettyServer.state_send;

import NettyServer.FrameHandler;
import NettyServer.state_receive.IStateReceive;
import io.netty.buffer.ByteBuf;

public class NameLengthStateS implements IStateReceive {
    private FrameHandler handler;

    public NameLengthStateS (FrameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void nameLength(ByteBuf in) {
        if (in.readableBytes() >= 1) {
//            int bytesRead = in.readableBytes(); //сколько байт доступно для чтения
//            handler.setNameLength(in.getByte(1));

            handler.setNameLength(in.readByte());
            handler.setState(handler.getNameStateS());
            System.out.println(" NAME_LENGTH_S: " + in.getByte(1));
        }
    }
}
