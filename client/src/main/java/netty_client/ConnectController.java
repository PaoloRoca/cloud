package netty_client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.InetSocketAddress;

public class ConnectController {
    @FXML private Label labelConnect;
    @FXML private TextField hostTField;
    @FXML private TextField portTField;
    @FXML private TextField userNameTField;
    @FXML private TextField passwordTField;
    @FXML private Button btnConnect;
    @FXML private Button btnDisconnect;

    private Client client;
    private MainController mainController;

    private Channel channel;
    private ChannelFuture channelFuture;
    private EventLoopGroup group;
    private Boolean connect;

    //TODO
//    private Logger logger = LoggerFactory.getLogger(ConnectController.class);

    public void connectToServer() {
        System.out.println("ConnectController.connectToServer");

        client = new Client();

        try {
            connect(client);
            client.setClientParam(hostTField.getText(), Integer.parseInt(portTField.getText().trim()),
                    userNameTField.getText(), passwordTField.getText());
            labelConnect.setText("Connecting OK");
            btnConnect.setDisable(true);
            btnDisconnect.setDisable(false);
            mainController.setClient (client);
            connect = true;

        } catch (Exception e) {
            e.printStackTrace();
            labelConnect.setText("Error connecting to the server");
            btnDisconnect.setDisable(true);
            connect = false;
        }
    }

    public void disconnectFromServer() {
        System.out.println("disconnectFromServer");

        try {
            stop();
            labelConnect.setText("Disconnect from the server");
            btnConnect.setDisable(false);
            btnDisconnect.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
            labelConnect.setText("Error disconnect from the server");
        }
    }

    public void setParent(MainController mainController) {
        this.mainController = mainController;
    }

    public void connect(Client client) throws Exception {
        group = new NioEventLoopGroup(); //Обработка событий

        Bootstrap b = new Bootstrap(); //Инициализация клиента
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", 8080)) //Подключение к серверу
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ClientReadFromServer(client)); //
//                        ch.pipeline().addLast(new ClientWriteToServer()); //
                    }
                });

        channelFuture = b.connect().sync(); //Все установлено, подключаемся к удаленному узлу
        channel = channelFuture.channel();
        client.setChannel(channel);
    }

    public void stop() throws InterruptedException {
        if (channelFuture != null) {
            channelFuture.channel().closeFuture();
        }
        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
