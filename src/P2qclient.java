import java.io.*;
import java.net.*;
import java.util.*;

public class P2qclient {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private static ObjectOutputStream obj;

	public static void main(String args[]) throws IOException {
		P2qclient client = new P2qclient();
		client.startConnection("127.0.0.1", 6666);
		String response = client.sendMessage("hello server");
		System.out.println(response);
	}

	public void startAction() {
		Scanner sr = new Scanner(System.in);
		while(true) {
			System.out.print("> ");
			String[] input = sr.nextLine().split(" ");
			String choice = input[0];
			if(choice.compareTo("p") == 0) {
				out.println("post");
				out.flush();
				pChoice();
			}
		}
	}

	private static void pChoice() {
		Question question = modelQuestion();
		try {
			obj.writeObject(question);
			obj.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//			readServer();
		System.out.println(question.questionText);
	}

	private static Question modelQuestion() {
		Question question = new Question();
		Scanner sr = new Scanner(System.in);
		question.tag = sr.nextLine();
		StringBuilder sb = new StringBuilder();
		while(sr.hasNextLine()) {
			String cur = sr.nextLine();
			if(cur.compareTo(".") == 0) {
				break;
			}
			sb.append(cur);
		}
		String description = sb.toString();
		question.questionText = description;
		String start = sr.nextLine();

		while(start.compareTo(".") != 0) {
			String end = start;
			sb = new StringBuilder();
			while(end.compareTo(".") != 0) {
				sb.append(end);
				end = sr.nextLine();
			}
			question.choice.add(sb.toString());
			start = sr.nextLine();
		}
		question.correctRes = sr.next();
		return question;
	}

	public void startConnection(String ip, int port) throws  IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		obj = new ObjectOutputStream(clientSocket.getOutputStream());
	}

	public String sendMessage(String msg) throws IOException {
		out.println(msg);
		String resp = in.readLine();
		return resp;
	}

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
}
