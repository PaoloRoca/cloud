package NettyServer.state_receive;

import NettyServer.CommandServer;
import NettyServer.Consumer;
import NettyServer.file_controller.ServerFileController;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FrameHandlerOop extends ChannelInboundHandlerAdapter {
    private Consumer consumer;

    private IStateReceive idleStateR;
    private IStateReceive nameLenStateR;
    private IStateReceive nameStateR;
    private IStateReceive fileNameStateR;
    private IStateReceive fileStateR;
    private IStateReceive state; //Для хранения состояния

    private Object msg = null;
    private ChannelHandlerContext ctx;

    private int receiveFileLength;  // Принято байт файла
    private byte nameLength; //Длина имени файла
    private OutputStream out;
    private long fileLength; //Длина данных файла

    public void initialize() {
        idleStateR = new IdleState(this);
        nameLenStateR = new NameLengthStateR(this);
        nameStateR = new NameStateR(this);
        fileNameStateR = new FileLengthStateR(this);
        fileStateR = new FileStateR(this);

        state = idleStateR;
    }

    void setState (IStateReceive state) {
        this.state = state;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("* FrameHandler.channelActive: ");
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
        ctx.fireChannelActive();

        //
        initialize();

        //TODO
        Path path = Paths.get("server_storage", "1");
        System.out.println(path);
        this.consumer = new Consumer(ctx, path);

        byte[] userFiles = ServerFileController.getFilesNameList(consumer.getUserDirectory());
        CommandServer.sendDirectoryStruct(ctx, userFiles);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("* FrameHandler.channelRead: ");
        ByteBuf in = ((ByteBuf) msg);

        while (in.readableBytes() > 0) {
            state.idle(in);
            state.nameLength(in);
            state.name(in);
            state.fileLength(in);
            state.file(in);
        }
        if (in.readableBytes() == 0) {
            in.release();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel().remoteAddress() + " disconnected");
    }

    public IStateReceive getIdleStateR() {
        return idleStateR;
    }

    public IStateReceive getNameLengthStateR() {
        return nameLenStateR;
    }

    public IStateReceive getNameStateR() {
        return nameStateR;
    }

    public IStateReceive getFileNameStateR() {
        return fileNameStateR;
    }

    public IStateReceive getFileStateR() {
        return fileStateR;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public OutputStream getOut() {
        return out;
    }

    public int getReceivedFileLength() {
        return receiveFileLength;
    }

    public byte getNameLength() {
        return nameLength;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public void setNameLength(byte nameLength) {
        this.nameLength = nameLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public void setReceiveFileLength(int receiveFileLength) {
        this.receiveFileLength = receiveFileLength;
    }

    public void addReceivedByte(int receivedFileLength) {
        this.receiveFileLength += receivedFileLength;
    }
}
