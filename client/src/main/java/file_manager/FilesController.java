package file_manager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FilesController {
    @FXML
    private ListView<FileInfo> filesList;
    @FXML
    public ListView<String> serverList;
    @FXML
    private TextField clientFiles;
    @FXML
    private TextField serverFiles;
    @FXML
    private Button btnDir, btnPreview, btnCopy, btnMove, btnDelete;

    //TODO Как получить ссылку этот контроллер из главного контроллера
    public static FilesController filesController;

    private int value = 0;
    private Path root;
    private Path selectedCopyFile;
    private Path selectedMoveFile;
    private Path selectSendFile;
    private String selectReceiveFile;

    public void initialize() {
        serverList.getItems().add("No connection!");
        this.filesController = this;

        filesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>() {
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        }else {
                            String formattedFileName = String.format("%-30s", item.getFilename());
                            String formattedFileSize = String.format("%,d bytes", item.getSize());
                            if(item.getSize() == -1L) {
                                formattedFileSize = String.format("%s", "[ DIR ]");
                            }
                            if(item.getSize() == -2L) {
                                formattedFileSize = String.format("");
                            }
                            String text = String.format("%s %-20s", formattedFileName, formattedFileSize);
                            setText(text);
                        }
                    }
                };
            }
        });
        //String dir = System.getProperty("user.dir");
        Path path = Paths.get("client_storage");
        goToPath(path);
    }

    public void showServerFiles (byte[] arr) {
        System.out.println("FilesController.showServerFiles");
        String str = new String(arr);

        String[] filesArr = str.split("-");
        List<String> files = new ArrayList<>(Arrays.asList(filesArr));

        serverList.getItems().clear();
        for (String s : files) {
            serverList.getItems().add(s);
        }
    }

    public void goToPath(Path path) {
        root = path;
        clientFiles.setText(root.toAbsolutePath().toString());
        filesList.getItems().clear();
        filesList.getItems().add(new FileInfo(FileInfo.UP_TOKEN, -2L));
        filesList.getItems().addAll(scanFiles(path));

        filesList.getItems().sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if (o1.getFilename().equals(FileInfo.UP_TOKEN)) {
                    return -1;
                }
                if ((int) Math.signum(o1.getSize()) == (int) Math.signum(o2.getSize())) {
                    return o1.getFilename().compareTo(o2.getFilename());
                }
                return Long.compare(o1.getSize(),o2.getSize());
            }
        });
    }

    //Метод выдает из каталога список файлов
    public List<FileInfo> scanFiles(Path root) {
        try {
            //Длинный вариант
//            List<FileInfo> out = new ArrayList<>();
//            List<Path> pathsInRoot = null;
//            pathsInRoot = Files.list(root).collect(Collectors.toList());
//            for (Path p : pathsInRoot) {
//                out.add(new FileInfo(p));
//            }
            //Короткий путь
            return Files.list(root).map(FileInfo::new).collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Files scan exception: " + root);
        }
    }

    public void filesListClicked(MouseEvent mouseEvent) {
        System.out.println("** FilesController.filesListClicked ");
        btnDir.setDisable(false);
        btnPreview.setDisable(false);
        btnCopy.setDisable(false);
        btnMove.setDisable(false);
        btnDelete.setDisable(false);

        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2) {
            if (fileInfo != null) {
                if (fileInfo.isDirectory()) { //Если fileInfo - директория
                    Path pathTo = root.resolve(fileInfo.getFilename());
                    goToPath(pathTo);
                }
                if (fileInfo.isUpElement()) {
                    Path pathTo = root.toAbsolutePath().getParent();
                    goToPath(pathTo);
                }
            }
        }
        //
        if (mouseEvent.getClickCount() == 1) {
            if (fileInfo != null) {
                if (!fileInfo.isDirectory() && !fileInfo.getFilename().equals(FileInfo.UP_TOKEN)) {
//                    selectSendFile = fileInfo.getFilename();
                    selectSendFile = root.resolve(fileInfo.getFilename());
                    System.out.println("Выбрали файл на клиенте: " + selectSendFile + " ");
                }
            }
        }
    }

    public void serverListClicked(MouseEvent mouseEvent) {
        System.out.println("** FilesController.serverListClicked ");
        btnDir.setDisable(true);
        btnPreview.setDisable(true);
        btnCopy.setDisable(true);
        btnMove.setDisable(true);
        btnDelete.setDisable(false);

        selectReceiveFile = serverList.getSelectionModel().getSelectedItem();

        System.out.println("Выбрали файл на сервере: " + selectReceiveFile + " ");
    }

    public void refresh() {
        goToPath(root);
    }

    public void catalog(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory");
        directoryChooser.setInitialDirectory(new File("C:"));

        File selectedDir = directoryChooser.showDialog(null);

        if (selectedDir == null) {
            System.out.println("Каталог не выбран");
        } else {
            goToPath(Paths.get(selectedDir.getAbsolutePath()));
        }
    }

    public void copy(ActionEvent actionEvent) {
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();

        if (selectedCopyFile == null && (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement())) {
            return;
        }

        if (selectedCopyFile == null) {
            selectedCopyFile = root.resolve(fileInfo.getFilename());
            ((Button) actionEvent.getSource()).setText("Paste: " + fileInfo.getFilename());
            return;
        }

        try {
            Files.copy(selectedCopyFile, root.resolve((selectedCopyFile.getFileName())), StandardCopyOption.REPLACE_EXISTING);
            selectedCopyFile = null;
            ((Button) actionEvent.getSource()).setText("Copy");
            refresh();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно скопировать файл");
            alert.showAndWait();
        }
    }

    public void move(ActionEvent actionEvent) {
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();

        if (selectedMoveFile == null && (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement())) {
            return;
        }

        if (selectedMoveFile == null) {
            selectedMoveFile = root.resolve(fileInfo.getFilename());
            ((Button) actionEvent.getSource()).setText("Move: " + fileInfo.getFilename());
            return;
        }

        try {
            Files.move(selectedMoveFile, root.resolve((selectedMoveFile.getFileName())), StandardCopyOption.REPLACE_EXISTING);
            selectedMoveFile = null;
            ((Button) actionEvent.getSource()).setText("Move");
            refresh();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно переместить файл");
            alert.showAndWait();
        }
    }

    public void delete(ActionEvent actionEvent) {
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();

        if (selectedCopyFile == null && (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement())) {
            return;
        }

        try {
            Files.delete(root.resolve(fileInfo.getFilename()));
            refresh();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно удалить файл");
            alert.showAndWait();
        }
    }

    public Path getSelectFileToSend() {
        return selectSendFile;
    }

    public String getSelectFileToReceive() {
        return selectReceiveFile;
    }

}
