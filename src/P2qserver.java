import java.io.*;
import java.net.*;
import java.util.*;

public class P2qserver {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	static int portNum = 6666;
	Map<Integer, Contest> map = new HashMap<>();

	public static void main(String[] args) throws IOException {
		P2qserver server = new P2qserver();
		portNum = server.getPortNum();
		server.start(portNum);
	}

	public void start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println(port);
		clientSocket = serverSocket.accept();
		new meisterHandler(clientSocket, this).start();
	}

	public void drop() {
		try {
			serverSocket.close();
			start(portNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getPortNum() throws IOException {
		ServerSocket sock = new ServerSocket(0);
		int port = sock.getLocalPort();
		sock.close();
		return port;
	}	
}