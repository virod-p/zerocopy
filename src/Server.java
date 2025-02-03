import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class Server {
    public static void main(String[] args) {
        String mode = "0"; // "1" for normal copy, "0" for zero copy
        String filePath = "test.mp4"; // ไฟล์ที่ Server จ ะส่งให้ Client

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is listening on port 5000...");
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                if ("1".equals(mode)) {
                    // normal copy
                    copy(filePath, socket);
                } else {
                    // zero copy
                    zeroCopy(filePath, socket);
                }
                System.out.println("File sent successfully!");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Normal copy method for server
    public static void copy(String from, Socket socket) throws IOException {
        byte[] data = new byte[1024]; // data[0] - data[1023]
        FileInputStream fis = new FileInputStream(from);
        OutputStream os = socket.getOutputStream();
        int bytesRead;
        while ((bytesRead = fis.read(data)) != -1) {
            os.write(data, 0, bytesRead);
        }
        os.flush();
        fis.close();
    }

    // Zero copy method for server
    public static void zeroCopy(String from, Socket socket) throws IOException {
        FileInputStream fis = new FileInputStream(from);
        FileChannel sourceChannel = fis.getChannel();
        WritableByteChannel destChannel = java.nio.channels.Channels.newChannel(socket.getOutputStream());
        sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        sourceChannel.close();
        fis.close();
    }
}
