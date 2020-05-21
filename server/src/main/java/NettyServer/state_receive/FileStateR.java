package NettyServer.state_receive;

import NettyServer.CommandServer;
import NettyServer.FrameHandler;
import NettyServer.file_controller.ServerFileController;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class FileStateR implements IStateReceive{
    private FrameHandler handler;

    public FileStateR(FrameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void file(ByteBuf in) throws IOException {
        while (in.readableBytes() > 0) {
            int size = in.readableBytes();
            in.readBytes(handler.getOut(), size);
            handler.addReceivedByte(size);
            //Test
            System.out.println(" Принято байтов: " + size + " осталось: " +
                    (handler.getFileLength() - handler.getReceivedFileLength()));

            if (handler.getFileLength() == handler.getReceivedFileLength()) {
                handler.setState(handler.getIdleStateR());
                System.out.println(" ++ File received");
                handler.getOut().close();
                //TODO доп. поток
                byte[] userFiles = ServerFileController.getFilesNameList(handler.getConsumer().getUserDirectory());
                CommandServer.sendDirectoryStruct(handler.getConsumer().getChannel(), userFiles);
                break;
            }
        }
    }
}
