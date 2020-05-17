import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * + Отработка события подключения/отключения к серверу, когда Сервер не работает
 * - Логирование (по уровням, в файл)
 * - Блокирование файла при чтении/записи
 * - Сообщения между контроллерами (при изменении состояний)
 * - Если повторно открыть окно (н-р, после подключения к серверу) и попробовать отключиться...
 *      (надо грузить в окно реальное состояние соединения)
 * - Доп.поток на клиенте
 * - Добавить сериализацию
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/mainframe.fxml"));
        primaryStage.setTitle("Simple File Manager");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
