import java.io.*;
import java.net.*;
import java.util.*;

public class meisterClient {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private static ObjectOutputStream obj;
	static meisterClient client;
	
	public static void main(String args[]) throws IOException {
		client = new meisterClient();
		try {
			client.startConnection(args[0],	Integer.parseInt(args[1]));
			client.startAction();
		} catch (ConnectException e) {
			System.out.println("Error: invalid port number: " + args[1]);
		}
	}

	public void startAction() {
		Scanner sr = new Scanner(System.in);
		while(true) {
			System.out.print("> ");
			String[] input = sr.nextLine().split(" ");
			String choice = input[0];
			if(choice.compareTo("p") == 0) {
				int number = Integer.parseInt(input[1]);
				out.println("post");
				client.pChoice(number);
			} else if(choice.compareTo("l") == 0) {
				out.println("list");
				readServer();
			} else if(choice.compareTo("g") == 0) {
				out.println("get");
				out.println(Integer.parseInt(input[1]));
				readServer();
			} else if(choice.compareTo("d") == 0) {
				out.println("delete");
				out.println( Integer.parseInt(input[1]));
				readServer();
			} else if(choice.compareTo("q") == 0) {
				try {
					out.println("quit");
					
					clientSocket.close();
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			} else if(choice.compareTo("s") == 0) {
				out.println("set");
				out.println(Integer.parseInt(input[1]));
				readServer();
			} else if(choice.compareTo("a") == 0) {
				out.println("add");
				out.println(Integer.parseInt(input[1]));
				out.println(Integer.parseInt(input[2]));
				readServer();
			} else if(choice.compareTo("b") == 0) {
				out.println("begin");
				out.println(Integer.parseInt(input[1]));
				readServer();
			} else if(choice.compareTo("r") == 0) {
				out.println("review");
				out.println(Integer.parseInt(input[1]));
				readServer();
			} else if(choice.compareTo("k") == 0) {
				out.println("kill");
				break;
			} else if(choice.compareTo("h") == 0) {
				System.out.println("Use the following commands: ");
				System.out.println("p: put a question in the bank");
				System.out.println("d: delete question from the bank");
				System.out.println("g: get question from the bank");
				System.out.println("r: review a contest");
				System.out.println("a: add a question to a contest");
				System.out.println("s: set a contest");
				System.out.println("l: list all contest");
				System.out.println("k: terminate the server");
				System.out.println("q: terminate the client");
				System.out.println("h: help");
			}
		}
	}

	private void pChoice(int number) {
		Question question = modelQuestion();
		question.questionNumber = number;
		try {
			obj.writeObject(question);
			obj.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readServer();
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
	
    private void readServer() {
        InputStreamReader in;
        try {
            in = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            String first = bf.readLine();
            System.out.println(first);
            while(bf.ready()) {
                System.out.println(bf.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
