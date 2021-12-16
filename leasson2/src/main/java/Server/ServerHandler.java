package Server;

import Client.FileInfo;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ServerHandler implements Runnable {

    private static final int BUFFER = 8192;

    private final DataOutputStream os;
    private final DataInputStream is;
    private final Path defaultDir = Paths.get("src/main/java/Server/files");
    private final byte[] buff;


    public ServerHandler(Socket socket) throws IOException {
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
            buff = new byte[8192];
        System.out.println("Client connected");
    }

    public List<String> getServerFileNames() throws IOException {
        return Files.list(defaultDir)
                .map(path -> new FileInfo(path).getName())
                .collect(Collectors.toList());
    }

    @Override
    public void run() {
        try {
            while (true){
                String command = is.readUTF().trim();
                System.out.println("command: " + command);
                switch (command) {
                    case "#list#":
                        List<String> list = getServerFileNames();
                        os.writeInt(list.size());
                        for (String s : list) {
                            os.writeUTF(s);
                        }
                        break;
                    case "#upload#": {
                        String fileName = is.readUTF();
                        long size = is.readLong();
                        try (FileOutputStream fos = new FileOutputStream(defaultDir.resolve(fileName).toFile())) {
                            for (int i = 0; i < (size + BUFFER - 1) / BUFFER; i++) {
                                int read = is.read(buff);
                                fos.write(buff, 0, read);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "#download#": {
                        String fileName = is.readUTF();
                        Path path = defaultDir.resolve(fileName);
                        os.writeLong(Files.size(path));
                        os.write(Files.readAllBytes(path));
                        break;
                    }
                }

                os.flush();
            }
        } catch (IOException e){
            System.out.println("client disconnected");
        }

    }
}
