import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class qserver {
	private static Socket clientSocket;
	private PrintWriter out;
	private BufferedReader bf;
	private static ObjectOutputStream obj;
	static meisterClient client;
	Scanner sr;
	int size = 0;

	public static void main(String[] args) {
		qserver cli = new qserver();
		cli.start();
		cli.startAction();

	}

	private void startAction() {
		sr = new Scanner(System.in);
		System.out.println("Please input a nickname:  ");
		String name = sr.nextLine();
		out.println(name);
		
		try {
			String garbage = bf.readLine();
			System.out.println(garbage);
			String res = bf.readLine();
			System.out.print("name response is : " + res);
			while(res.compareTo("ok") != 0) {
				System.out.println("Error, Please input a nickname again:  ");
				name = sr.next();
				out.println("name");
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
			System.out.println(size);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Hello " + name + " get ready for contest!");		
		while(true) {
			startQuestion();
		}
	}
	
	public void startQuestion() {
		readServer();
		System.out.println("please enter your choice: ");
		String ans = sr.next();
		System.out.println("your chioce is: " + ans);
		out.println(ans);
		readServer();	
	}

    private void readServer() {
        try {
//            String first = bf.readLine();
//            System.out.println(first);
            while(bf.ready()) {
                System.out.println(bf.readLine());
            }
            System.out.println("bf ended");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
	
	public void start() {
		try {
			clientSocket = new Socket("localhost", 57587);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			obj = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
