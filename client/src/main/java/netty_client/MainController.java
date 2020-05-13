package netty_client;

import file_manager.FilesController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainController {
    @FXML
    private Text textClient;
    @FXML
    private ConnectController connectController;

    private Client client;

    public void initialize() {
        // controller available in initialize method
        System.out.println("Current value: ");
    }

    public void menuItemFileAction(ActionEvent actionEvent) {
        if (connectController != null) {
            connectController.disconnectFromServer();
        }
        Platform.exit();
    }

    public void connect(ActionEvent actionEvent) {
        try {
            //TODO загрузить реальное
            Stage stageFind = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/connection.fxml"));
            Parent root = loader.load();
            stageFind.setTitle("Connect to server");
            stageFind.setMinHeight(200);
            stageFind.setMinWidth(150);
            stageFind.setResizable(false);
            stageFind.setScene(new Scene(root));
//            stageFind.getIcons().add(new Image("image/connect"));
            stageFind.initModality(Modality.NONE);
            stageFind.show();

            connectController = loader.getController(); //получаем контроллер
            connectController.setParent(this); //Устанавливаем предка

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClient (Client client) {
        this.client = client;
    }

    public void send(ActionEvent actionEvent) throws IOException, InterruptedException {
        System.out.println("** MainController.send");
        //TODO не должна работать пока нет подключения к серверу

        //Отладка
//        ChannelPipeline pipeline = connectController.getChannel().pipeline();
//        pipeline.writeAndFlush(Unpooled.copiedBuffer("123 456",CharsetUtil.UTF_8));

        String str = FilesController.filesController.getSelectSendFile();
        System.out.println("Файл для передачи: " + str);

        //Данные файла
        Path path = Paths.get("1", str);
        Path file = path.getFileName();
        System.out.println("path.getParent(): " + path.getParent());
        InputStream inputStream = Files.newInputStream(path);

        int bufferSize = 32000;
        byte[] fileBuffer = new byte[inputStream.available()];

        while (inputStream.available() > 0) {
            if (inputStream.available() < 1024) bufferSize = inputStream.available();
            inputStream.read(fileBuffer, 0, bufferSize);
            //TODO если файл больше 32000
        }

        int capacity =
                1 + //command
                1 + //file name length
                str.trim().getBytes().length + //name of file
                4 +  //data length
                fileBuffer.length; //data

        ByteBuf byteBuf = Unpooled.buffer(capacity);
        byteBuf.writeBytes("1".getBytes()); //Команда
        byteBuf.writeByte(str.trim().getBytes().length); //Длина имени файла не (больше 254)
        byteBuf.writeBytes(str.getBytes()); //Имя файла
        byteBuf.writeInt(fileBuffer.length); // Размер данных
        byteBuf.writeBytes(fileBuffer); // Данные файла

        ChannelPipeline pipeline = connectController.getChannel().pipeline();
        pipeline.writeAndFlush(byteBuf);

//        StringBuilder str = new StringBuilder();
//        for (int i = 0; i < byteBuf.readableBytes(); i++) {
//            System.out.print(" " + byteBuf.getByte(i));
//        }
//        System.out.println();

    }

    public void receive(ActionEvent actionEvent) {

    }
}
