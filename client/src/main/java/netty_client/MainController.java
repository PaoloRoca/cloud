package netty_client;

import file_manager.FilesController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {
    @FXML
    private Text textClient;
    @FXML
    private ConnectController connectController;
    @FXML
    private Button btnSend, btnReceive;

    private Client client;

    public void initialize() {
        // controller available in initialize method
        System.out.println("MainController.initialize() ");
    }

    public void menuItemFileAction(ActionEvent actionEvent) {
        if (connectController != null) {
            connectController.disconnectFromServer();
        }
        Platform.exit();
        System.out.println("menuItemFileAction");
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
            connectController.setParent(this); //Устанавливаем ему предка

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClient (Client client) {
        this.client = client;
    }

    public void send(ActionEvent actionEvent) throws IOException, InterruptedException {
        System.out.println("** MainController.send");
        //TODO Кнопка не должна работать пока нет подключения к серверу
        CommandClient.sendFileToServer (connectController.getChannel());

    }

    public void receive(ActionEvent actionEvent) {
        System.out.println("** MainController.receive");
        //TODO проверка подключения к серверу
        String name = FilesController.filesController.getSelectFileToReceive();
        CommandClient.requestFileFromServer (connectController.getChannel(), name);
    }
}
