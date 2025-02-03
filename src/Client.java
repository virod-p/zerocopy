import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.Socket;

import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;


public class Client {
    public static void main(String[] args) {
        String mode = "0"; // "1" for normal copy, "0" for zero copy
        String destinationPath = "test_downloaded.deb"; // ไฟล์ที่ Client จะบันทึกข้อมูลที่รับจาก Server

        try (Socket socket = new Socket("192.168.1.118", 5000)) {
            System.out.println("Connected to server");

            if ("1".equals(mode)) {
                // normal copy
                copy(socket, destinationPath);
            } else {
                // zero copy
                zeroCopy(socket, destinationPath);
            }
            System.out.println("File received successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Normal copy method for client
    public static void copy(Socket socket, String to) throws IOException {
        byte[] data = new byte[1024];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(to);
        int bytesRead;
        long start = System.currentTimeMillis();
        while ((bytesRead = is.read(data)) != -1) {
            fos.write(data, 0, bytesRead);
        }
        fos.flush();
        fos.close();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Time: " + time + " millisecond");
    }

    // Zero copy method for client
    public static void zeroCopy(Socket socket, String to) throws IOException {
        FileOutputStream fos = new FileOutputStream(to);
        FileChannel destChannel = fos.getChannel();
        ReadableByteChannel sourceChannel = java.nio.channels.Channels.newChannel(socket.getInputStream());
        long start = System.currentTimeMillis();
        destChannel.transferFrom(sourceChannel, 0, Long.MAX_VALUE);
        destChannel.close();
        fos.close();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Time: " + time + " millisecond");
    }
}
