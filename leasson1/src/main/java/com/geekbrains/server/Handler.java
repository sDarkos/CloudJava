package com.geekbrains.server;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {

    private final DataInputStream is;
    private final DataOutputStream os;
    private final String path = "src/main/java/com/geekbrains/server/files";
    private final File dir = new File(path);
    private final byte[] buf = new byte[8192];

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client accepted...");
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = is.readUTF().trim();
                System.out.println("received command: " + command);
                switch (command) {
                    case "list": {
                        File[] arrFiles = dir.listFiles();
                        if (arrFiles != null) {
                            if (arrFiles.length == 0){
                                os.writeUTF("Folder is empty");
                            } else {
                                os.writeUTF("---");
                                for (int i = 0; i < arrFiles.length; i++) {
                                    os.writeUTF(arrFiles[i].getName());
                                }
                                os.writeUTF("---");
                            }
                        } else {
                            os.writeUTF("ups");
                        }
                        break;
                    }

                    case "upload":
                        os.writeUTF("Enter file path");
                        String userFilePath = is.readUTF();
                        os.writeUTF("File start upload");
                        os.writeUTF("upload");
                        os.writeUTF(userFilePath);
                        String userFileName = is.readUTF();
                        if (!userFileName.equals("cancel.cancel")) {
                            File file = new File(path + "/" + userFileName);
                            FileOutputStream fos = new FileOutputStream(file);
                            int count;
                            while (is.available() > 0) {
                                count = is.read(buf);
                                fos.write(buf, 0, count);
                            }
                            fos.flush();
                            fos.close();
                            os.writeUTF("File upload");
                            os.writeUTF("---");
                        }
                        break;

                }
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
