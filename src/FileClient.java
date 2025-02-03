import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import java.net.Socket;

public class FileClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // ใช้ "localhost" ถ้า Client และ Server รันบนเครื่องเดียวกัน
        int port = 12345; // ใช้พอร์ตเดียวกันกับ Server

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(serverAddress, port));
            System.out.println("Connected to server: " + serverAddress);

            // รับไฟล์
            File file = new File("test_downloaded.mp4"); // กำหนด path ที่ต้องการบันทึกไฟล์
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                    FileChannel fileChannel = fileOutputStream.getChannel()) {

                ByteBuffer buffer = ByteBuffer.allocate(4096); // ใช้ allocate แทน allocateDirect
                int bytesRead;

                // อ่านข้อมูลจาก InputStream ของ Socket
                while ((bytesRead = socket.getInputStream().read(buffer.array())) != -1) {
                    buffer.limit(bytesRead); // จำกัดขนาด buffer ให้ตรงกับจำนวนที่อ่าน
                    buffer.position(0); // รีเซ็ต position ของ buffer
                    fileChannel.write(buffer); // เขียนข้อมูลลงใน FileChannel
                    buffer.clear(); // เคลียร์ buffer สำหรับการอ่านครั้งถัดไป
                }
                System.out.println("File received successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
