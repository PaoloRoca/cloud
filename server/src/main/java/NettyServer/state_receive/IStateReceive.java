package NettyServer.state_receive;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface IStateReceive {
    default void idle(ByteBuf in) {
    }

    default void nameLength(ByteBuf in) {
    }

    default void name(ByteBuf in) throws Exception {
    }

    default void fileLength(ByteBuf in) {
    }

    default void file(ByteBuf in) throws IOException {

    }
}
