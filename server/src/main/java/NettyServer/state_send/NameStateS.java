package NettyServer.state_send;

import NettyServer.CommandServer;
import NettyServer.FrameHandler;
import NettyServer.state_receive.IStateReceive;
import io.netty.buffer.ByteBuf;

public class NameStateS implements IStateReceive {
    private FrameHandler handler;

    public NameStateS(FrameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void name(ByteBuf in) throws Exception {
//        int bytesRead = in.readableBytes(); //сколько байт доступно для чтения
        if (in.readableBytes() >= handler.getNameLength()) {
            byte[] fileName = new byte[handler.getNameLength()];
            in.readBytes(fileName);
            String file = new String(fileName);
            System.out.println(" FILE_NAME_S: " + file);
            CommandServer.sendFileToClient (handler.getCtx(), handler.getConsumer(), file);
            handler.setState(handler.getIdleStateR());
        }
    }
}
