package Client;

import FileModel.FileInfo;
import Model.AbstractMessage;
import Model.FileListMessage;
import Model.FileMessage;
import Model.FileRequestMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientController implements Initializable {

    private static final int BUFFER = 8192;

    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Path defaultDir = Paths.get(System.getProperty("user.home"));
    public ListView<String> serverLIst;
    public ListView<FileInfo> clientList;
    private byte[] buff;

    public void read(){
        try {
            while (true){
                AbstractMessage msg = (AbstractMessage) is.readObject();
                switch (msg.getMessageType()){
                    case FILE:
                        FileMessage fileMessage = (FileMessage) msg;
                        Files.write(
                                defaultDir.resolve(fileMessage.getFileName()),
                                fileMessage.getBytes()
                                );
                        Platform.runLater(() -> {
                            try {
                                clientList.getItems().clear();
                                clientList.getItems().addAll(getClientFileNames());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    case FILE_LIST:
                        FileListMessage fileListMessage = (FileListMessage) msg;
                        Platform.runLater(() -> {
                            serverLIst.getItems().clear();
                            serverLIst.getItems().addAll(fileListMessage.getFiles());
                        });
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientList.getSelectionModel().getSelectedItem().getName();
        Path filePath = defaultDir.resolve(fileName);
        os.writeObject(new FileMessage(filePath));
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverLIst.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequestMessage(fileName));
    }

    public void back(ActionEvent actionEvent) {
        if (defaultDir.getParent() != null)
        {
            defaultDir = defaultDir.getParent();
            setClientList();
        }
    }

    public void CreateFolderInClient(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CreateFolder.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Create new folder");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public List<FileInfo> getClientFileNames() throws IOException {
        return Files.list(defaultDir)
                .map(FileInfo::new)
                .collect(Collectors.toList());
    }

    public void setClientList() {
        try {
            clientList.getItems().clear();
            clientList.getItems().addAll(getClientFileNames());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            buff = new byte[8192];
            setClientList();
            clientList.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        Path path = clientList.getSelectionModel().getSelectedItem().getPath();
                        if (Files.isDirectory(path)){
                            defaultDir = path;
                            setClientList();
                        }
                    }
        });
            clientList.setCellFactory(list -> new ListCell<FileInfo>(){
                @Override
                protected void updateItem(FileInfo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null){
                        setText(item.getName());
                    }

                }

            });
            os.writeObject(new FileListMessage(defaultDir));
            Thread thread = new Thread(this::read);
            thread.setDaemon(true);
            thread.start();
            System.out.println("connected to server");
        } catch (IOException e) {
            System.out.println("disconnected");
        }

    }
}
