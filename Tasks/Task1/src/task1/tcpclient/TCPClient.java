package task1.tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    public TCPClient() {
    }
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        Socket clientSocket = new Socket(hostname, port);
        clientSocket.getOutputStream().write(toServerBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] fromServerBuffer = new byte[1024];
        while (true){
            int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
            if (fromServerLength == -1){
                break;
            }
                out.write(fromServerBuffer, 0, fromServerLength);
        }
        clientSocket.close();
        return out.toByteArray();
    }
}