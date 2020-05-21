package NettyServer.state_receive;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;

public class FileLengthStateR implements IStateReceive {
    private FrameHandlerOop handler;

    public FileLengthStateR(FrameHandlerOop handler) {
        this.handler = handler;
    }

    @Override
    public void fileLength(ByteBuf in) {
//        int bytesRead = in.readableBytes(); //сколько байт доступно для чтения
        if (in.readableBytes() >= 8) {
            long length = in.readLong();
            handler.setFileLength(length);
            handler.setState(handler.getFileStateR());
            //Test
            System.out.println(" FILE_LENGTH: " + length);
        }
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}
