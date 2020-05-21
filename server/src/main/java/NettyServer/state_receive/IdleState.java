package NettyServer.state_receive;

import NettyServer.FrameHandler;
import io.netty.buffer.ByteBuf;

public class IdleState implements IStateReceive {
    private final byte COMMAND_RECEIVE_FILE = 49; //1 Получение файла с Клиента
    private final byte COMMAND_SEND_FILE = 51; //3 Отправка файла Клиенту

    private FrameHandler handler;

    public IdleState(FrameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void idle(ByteBuf in) {
//        int bytesRead = in.readableBytes(); //сколько байт доступно для чтения
        byte read = in.readByte();
        if (read == COMMAND_RECEIVE_FILE) {
            handler.setReceiveFileLength(0);
            handler.setState(handler.getNameLengthStateR());
            System.out.println("COMMAND 1/49 (прием файла): " + read);
        }
        else if (read == COMMAND_SEND_FILE) {
            handler.setReceiveFileLength(0);
            handler.setState(handler.getNameLenStateS());
            System.out.println("COMMAND 3/51 (Запрос на передачу файла): " + read);
        }
        else {
            System.out.println("!!! ERROR: Invalid first byte - " + read);
            handler.setState(handler.getIdleStateR());
        }
    }

}
