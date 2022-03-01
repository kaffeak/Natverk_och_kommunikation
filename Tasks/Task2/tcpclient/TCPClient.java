package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    public boolean shutdown;
	public Integer timeout;
	public Integer BUFFERSIZE;
	public Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
		this.shutdown = shutdown;
		this.limit = limit;
		if(limit != null){
			BUFFERSIZE = limit;
		}
		else {
			BUFFERSIZE = 1024;
		}	
		if(timeout == null){
			this.timeout = 0;
		}
		else{
			this.timeout = timeout;
		}
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
		byte[] fromServerBuffer = new byte[BUFFERSIZE];
		Socket clientSocket = new Socket(hostname, port);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
		clientSocket.setSoTimeout(timeout);
        clientSocket.getOutputStream().write(toServerBytes);
		if(shutdown){
			clientSocket.close();
			return toServerBytes;
		}
		if(limit != null){
			int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
			out.write(fromServerBuffer, 0, fromServerLength);
		}
		else {
			while (true){
				int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
				if (fromServerLength == -1){
					break;
				}
					out.write(fromServerBuffer, 0, fromServerLength);
			}
		}
		out.close();
        clientSocket.close();
		}
		catch(SocketTimeoutException e){
			clientSocket.close();
			System.out.println("rip timeout exeption: " + e);
		}
		return out.toByteArray();
	}
}
