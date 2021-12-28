package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientController implements Initializable {

    private static final int BUFFER = 8192;

    private DataInputStream is;
    private DataOutputStream os;
    private final Path defaultDir = Paths.get(System.getProperty("user.home"));
    public ListView<String> serverLIst;
    public ListView<String> clientList;
    private byte[] buff;


    public void upload(ActionEvent actionEvent) {
        String fileName = clientList.getSelectionModel().getSelectedItem();
        if (fileName != null){
            System.out.println("start upload file: " + fileName);
            Path path = defaultDir.resolve(fileName);
            try {
                os.writeUTF("#upload#");
                os.writeUTF(fileName);
                os.writeLong(Files.size(path));
                os.write(Files.readAllBytes(path));
                os.flush();
                System.out.println("Upload done");
                refresh(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void download(ActionEvent actionEvent)  {
        String fileName = serverLIst.getSelectionModel().getSelectedItem();
        if (fileName != null){
            System.out.println("start download file: " + fileName);
            try {
                os.writeUTF("#download#");
                os.writeUTF(fileName);
                long size = is.readLong();
                try(FileOutputStream fos = new FileOutputStream(defaultDir.resolve(fileName).toFile())) {
                    for (int i = 0; i < (size + BUFFER - 1) / BUFFER; i++) {
                        int read = is.read(buff);
                        fos.write(buff, 0, read);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("Upload done");
                refresh(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh(ActionEvent actionEvent) {
        clientList.getItems().clear();
        setClientList();
        serverLIst.getItems().clear();
        setServerLIst();
        System.out.println("refresh done");
    }

    public List<String> getClientFileNames() throws IOException {
        return Files.list(defaultDir)
                .map(path -> new FileInfo(path).getName())
                .collect(Collectors.toList());
    }

    public void setServerLIst() {
        try {
            os.writeUTF("#list#");
            int count = is.readInt();
            for (int i = 0; i < count; i++) {
                String name = is.readUTF();
                Platform.runLater(() -> serverLIst.getItems().add(name));
            }
            os.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setClientList() {
        try {
            clientList.getItems().addAll(getClientFileNames());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            buff = new byte[8192];
            setClientList();
            setServerLIst();
            System.out.println("connected to server");
        } catch (IOException e) {
            System.out.println("disconnected");
        }

    }
}
