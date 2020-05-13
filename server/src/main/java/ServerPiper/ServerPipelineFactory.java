package ServerPiper;

import garbage.ConsumerHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * Как будут обрабатываться подключения и пакеты клиента, определяет PipelineFactory,
 * которая при открытии канала с клиентом создаёт для него pipeline,
 * в котором определены обработчики событий, которые происходят на канале.
 * Функция Channels.pipeline() создаёт новый pipeline с переданными ей обработчиками.
 */
public class ServerPipelineFactory implements ChannelPipelineFactory {

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

//        ProtocolDecoder prtDecoder = new ProtocolDecoder();
//        ProtocolEncoder prtEncoder = new ProtocolEncoder();
//
//        return Channels.pipeline(prtDecoder, prtEncoder,
//                new ClientHandler(prtDecoder, prtEncoder));

//        pipeline.addLast("decoder", (ChannelHandler) new SafeByteToMessageDecoder());
//        pipeline.addLast("encoder", new ProtocolEncoder());

        pipeline.addLast("decoder", new ProtocolDecoder());
        pipeline.addLast("client", new ConsumerHandler());

        return pipeline;
    }
}
