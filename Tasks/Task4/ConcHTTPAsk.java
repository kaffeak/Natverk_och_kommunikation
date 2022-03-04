import java.net.*;

import javax.lang.model.util.ElementScanner6;

import tcpclient.TCPClient;

import java.io.*;

public class ConcHTTPAsk {
    public static void main( String[] args) throws IOException, InterruptedException{
        
		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

		Thread[] threads = new Thread[8];

		for(int i = 0; i < threads.length; i++){
			MutlipleClients mc = new MutlipleClients(serverSocket);
			threads[i] = new Thread(mc);
			threads[i].start();
		}

		for(int i = 0; i < threads.length; i++){
			threads[i].join();
		}
		System.out.println("Done");
    }

	public static class MutlipleClients implements Runnable{

		ServerSocket serverSocket;

		public MutlipleClients(ServerSocket serverSocket){
			this.serverSocket = serverSocket;
		}

		@Override
		public void run(){
			int BUFFERSIZE = 1024;

		while(true){
			try{
			Socket clientSocket = serverSocket.accept();
            OutputStream output = clientSocket.getOutputStream();
            byte[] fromServerBuffer = new byte[BUFFERSIZE];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int fromServerLength;
            do{
                fromServerLength = clientSocket.getInputStream().read(fromServerBuffer, 0, BUFFERSIZE);
                outputStream.write(fromServerBuffer, 0, fromServerLength);
            } while(fromServerLength == BUFFERSIZE);

			String string = outputStream.toString();
			System.out.println(string);
			String[] arguments = string.split("[? &=/\n]");
			boolean shutdown = false;
			Integer timeout = null;
			Integer limit = null;
			String hostname = null;
			int port = 0;
			byte[] userInputBytes = new byte[0];
			Boolean ask = false;
			Boolean get = false;
			Boolean http = false;	

			for(int i = 0; i<arguments.length; i++){
				switch(arguments[i]){
					case "hostname": hostname = arguments[i+1];
						//i++; 
						break;
					case "limit": limit = Integer.parseInt(arguments[i+1]);
						//i++;
						break;
					case "shutdown": shutdown = Boolean.parseBoolean(arguments[i+1]);
						//i++;
						break;
					case "timeout": timeout = Integer.parseInt(arguments[i+1]);
						//i++;
						break;
					case "port": 
					System.out.println(arguments[i+1]);
					port = Integer.parseInt(arguments[i+1]);
						//i++;
						break;
					case "string": userInputBytes = arguments[i+1].getBytes("UTF-8");
						//i++;
						break;
					case "ask": ask = true;
						//i++;
						break;
					case "GET": get = true;
						break;
					case "HTTP": http = true;
						break;
				}
			}

			try{
				if(ask){
					if(hostname != null && port != 0 && get && http){
						TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
						byte[] fromClient = tcpClient.askServer(hostname, port, userInputBytes);
						output.write("HTTP/1.1 200 OK\r\n".getBytes());
						output.write("Content-Type: text/plain\r\n".getBytes());
						
						output.write("\r\n".getBytes());
						output.write(new String(fromClient, 0, fromClient.length, "UTF-8").getBytes());
					}
					else{
						output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
						output.write("Content-Type: text/plain\r\n".getBytes());
						output.write("\r\n".getBytes());
						output.write("<h1>400 Bad Request</h1>".getBytes());
					}
				}
				else{
					output.write("HTTP/1.1 404 Bad Request\r\n".getBytes());
					output.write("Content-Type: text/plain\r\n".getBytes());
					output.write("\r\n".getBytes());
					output.write("<h1>404 Not Found</h1>".getBytes());
				}
			}
			catch(UnknownHostException e){
				System.out.println(e);
				output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());					
				output.write("Content-Type: text/plain\r\n".getBytes());
				output.write("\r\n".getBytes());
				output.write("<h1>400 Bad Request</h1>".getBytes());
			}
			output.flush();
			output.close();
			}
			catch(NumberFormatException | IOException e){
				e.printStackTrace();
			}
		}
	}
	
		
	}
}

