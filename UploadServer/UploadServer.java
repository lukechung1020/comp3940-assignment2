import java.net.*;
import java.io.*;
public class UploadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8082);
            System.out.println("UploadServer: Server started on port 8082");
        } catch (IOException e) {
            System.err.println("UploadServer: Could not listen on port: 8082.");
            System.exit(-1);
        }
        while (true) {
            System.out.println("UploadServer: Waiting for client connection...");
	    new UploadServerThread(serverSocket.accept()).start();
        }
    }
}
