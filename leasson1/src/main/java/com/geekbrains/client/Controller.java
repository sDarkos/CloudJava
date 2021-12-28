package com.geekbrains.client;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller implements Initializable {


    private DataInputStream is;
    private DataOutputStream os;
    private final byte[] bytes = new byte[8192];
    public TextField input;
    public TextArea output;

    public void sendMessage(ActionEvent actionEvent) throws IOException {;
            os.writeUTF(input.getText());
            input.clear();
    }

    private void read() {
        try {
            while (true) {
                String message = is.readUTF();
                if (message.equals("upload")){
                    String path = is.readUTF();
                    upload(path);
                } else {
                    output.appendText(message + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(String path) {
        File file = new File(path);

        FileInputStream fis =  null;

        boolean fileOK = true;

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            output.appendText("File not fond\n");
            fileOK = false;
        }
        if (fileOK) {
            try {
                os.writeUTF(file.getName());
                int count;
                while ((count = fis.read(bytes)) > 0) {
                    os.write(bytes, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                output.appendText("ups\n");
            }
        } else {
            try {
                os.writeUTF("cancel.cancel");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(this::read);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
