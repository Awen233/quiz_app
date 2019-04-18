import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ContestClient {
	private static Socket clientSocket;
	private PrintWriter out;
	private BufferedReader bf;
	private static ObjectOutputStream obj;
	static meisterClient client;
	Scanner sr;
	int size = 0;
	int count = 0;
	
	public static void main(String[] args) {
		ContestClient cli = new ContestClient();
		try {
			cli.start(args);
			cli.startAction();
		} catch (Exception e) {
			System.out.println("Error: invalid port number: " + args[1]);
		}
	}

	private void startAction() {
		sr = new Scanner(System.in);
		System.out.print("Please input a nickname:  ");
		String name = sr.nextLine();
		out.println(name);
		
		try {
			String garbage = bf.readLine();
			String res = bf.readLine();
			while(res.compareTo("ok") != 0) {
				System.out.println("Error, Nickname " + name + " is already in use");
				System.out.print("Please input a nickname:  ");
				name = sr.next();
				out.println(name);
				res = bf.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String x = bf.readLine();
			size = Integer.parseInt(x);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Hello " + name + " get ready for contest!");		
		while(true) {
			startQuestion();
			count++;
			if(size == count ) {
				break;
			}
		}
		System.out.println("The contest is over, thanks for playing " + name + "!");
	}
	
	public void startQuestion() {
		readServer();
		System.out.print("please enter your choice: ");
		String ans = sr.next();
		out.println(ans);
		readServer();
	}

    private void readServer() {
        try {
            String first = bf.readLine();
            System.out.println(first);
            while(bf.ready()) {
                System.out.println(bf.readLine());
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
	
	public void start(String[] args) {
		try {
			clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			obj = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
