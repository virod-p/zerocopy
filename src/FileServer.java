import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class FileServer {
    public static void main(String[] args) {
        int port = 12345; // กำหนดหมายเลขพอร์ตที่ใช้ในการเชื่อมต่อ

        // เปิด ServerSocketChannel เพื่อรอการเชื่อมต่อจาก client
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port)); // ผูกช่องทางไปยังพอร์ตที่กำหนดไว้
            System.out.println("Server is listening on port " + port); // แสดงข้อความว่ากำลังรอการเชื่อมต่อ

            // เริ่ม loop เพื่อรอรับการเชื่อมต่อจาก client
            while (true) {
                // รอการเชื่อมต่อจาก client
                try (SocketChannel socketChannel = serverSocketChannel.accept()) {
                    System.out.println("Client connected: " + socketChannel.getRemoteAddress()); // แสดง IP ของ client ที่เชื่อมต่อ

                    // กำหนดไฟล์ที่ต้องการส่งให้ client
                    File file = new File("test.mp4"); // ระบุ path ของไฟล์ที่ต้องการส่ง

                    // เปิด FileInputStream และสร้าง FileChannel สำหรับการอ่านข้อมูลจากไฟล์
                    try (FileInputStream fileInputStream = new FileInputStream(file);
                         FileChannel fileChannel = fileInputStream.getChannel()) {

                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096); // สร้าง ByteBuffer ขนาด 4096 ไบต์เพื่อใช้ส่งข้อมูล
                        long position = 0; // กำหนดตำแหน่งเริ่มต้นในการอ่านไฟล์
                        long size = fileChannel.size(); // ขนาดทั้งหมดของไฟล์

                        // เริ่มส่งข้อมูลจากไฟล์ไปยัง client
                        while (position < size) {
                            long transferred = fileChannel.transferTo(position, buffer.capacity(), socketChannel); // ส่งข้อมูลจากตำแหน่งที่กำหนดไปยัง client
                            position += transferred; // อัพเดทตำแหน่งการส่ง
                        }
                        System.out.println("File sent successfully!"); // แสดงข้อความเมื่อไฟล์ถูกส่งเรียบร้อยแล้ว
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // จัดการข้อผิดพลาดที่อาจเกิดขึ้นและแสดง stack trace
        }
    }
}
