import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class meisterHandler extends Thread{
	private Socket clientSocket;
	private PrintWriter out;
	private Scanner in;
	private ObjectInputStream obj;
	Map<Integer, Question> bank;
	Map<Integer, Contest> contests = new HashMap<>();
	P2qserver callback;

	public meisterHandler(Socket socket, P2qserver callback) {
		this.clientSocket = socket;
		this.callback = callback;
	}
	
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
			obj = new ObjectInputStream(clientSocket.getInputStream());
			updateMap();
	
			if(!in.hasNext()) {
				return;
			}
			String choice = in.next();
			while(choice.compareTo("quit") != 0 ) {
				switch (choice) {
				case "post" : handlePost(); break;
				case "list" : handleList(); break;
				case "get"  : handleGet(); break;
				case "delete": handleDelete(); break;
				case "kill" : handleKill(); break;
				case "set" : handleSet(); break;
				case "add": handleAdd(); break;
				case "begin" : handleBegin(); break;
				case "review" : handleReview(); break;
				}
				if(!in.hasNext()) {
					return;
				}
				choice = in.next();
			}
	
			handleQuit();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void handleKill() {
		storeBank();
		System.exit(0);
	}

	private void handleReview() {
		int num = in.nextInt();
		Contest contest = contests.get(num);
		if(contest == null) {
			out.println("Error: Contest " + num + " does not exist");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(num);
		sb.append("\t");
		if(!contest.status) {
			sb.append(contest.question.size() + " questions, not run");
			out.println(sb.toString());
			return;
		}
		sb.append(contest.question.size() + " questions, run, average correct " + contest.averageCorrect + "; maximum correct: " + contest.maxScore);
		sb.append("\n");
		for(Question q : contest.question) {
			Integer x = contest.questionHist.get(q);
			int s = x == null ? 0 : x;
			sb.append(" \t" + q.questionNumber + "\t" + s + "%");
			sb.append("\n");
		}
		out.println(sb.toString());
	}

	private void handleBegin() {
		int num = in.nextInt();
		if(!contests.containsKey(num)) {
			out.println("Error: contest " + num + " not found");
			return;
		}
		int portNum = 0;
		try {
			portNum = getPortNum();
			out.println(portNum);
			System.out.println("contest " + num + " started on port " + portNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new ContestHandler(portNum, contests.get(num)).run();
	}

	private void handleAdd() {
		int contestNum = in.nextInt();
		int questionNum = in.nextInt();
		if(!contests.containsKey(contestNum)) {
			out.println("Error: Contest " + contestNum + " does not exists");
		} else if (!bank.containsKey(questionNum)) {
			out.println("Error: Question " + questionNum + " does not exists");
		} else {
			contests.get(contestNum).question.add(bank.get(questionNum));
			out.println("Added question " + questionNum + " to the contest " + contestNum);
		}
	}

	private void handleSet() {
		int num = in.nextInt();
		if(contests.containsKey(num)) {
			out.println("Contest " + num + " already exists");
		} else {
			contests.put(num, new Contest());
			out.println("contest " + num + " is set");
		}
	}

	private void handleQuit() {
		storeBank();
		callback.drop();
	}

	private void handleDelete() {
		int num = in.nextInt();
		if(bank.containsKey(num)) {
			bank.remove(num);
			storeBank();
			out.println("delete question " + num);
		}  else {
			out.println("Error: question " + num + " not found");
		}
	}

	private void handleGet() {
		
		int num = in.nextInt();
	
		if(bank.containsKey(num)) {
			Question q = bank.get(num);
			printQuestion(q);
		}  else {
			out.println("Error: question " + num + " not found");
		}
	}

	private void printQuestion(Question question) {
		StringBuilder sb = new StringBuilder();
		sb.append(question.tag + "\n");
		sb.append(question.questionText + "\n");
		sb.append("." + "\n");
		List<String> choices = question.choice;
		for(String s : choices) {
			sb.append(s + "\n");
			sb.append("." + "\n");
		}
		sb.append("." + "\n");
		sb.append(question.correctRes);
		out.println(sb.toString());
	}

	private void handleList() {
		if(contests.size() == 0) {
			out.println("not contest is running");
		}
		for(Integer i : contests.keySet()) {
			Contest cur = contests.get(i);
			if(!cur.status) {
				out.println(i + "\t" + "not run");
			} else {
				out.println(i + "\t" + cur.question.size() + " questions, run, average correct " + cur.averageCorrect + "; maximum correct: " + cur.maxScore);
			}
		}
	}

	private void handlePost() {
		try {
			Question question = (Question)obj.readObject();
			if(bank.containsKey(question.questionNumber)) {
				out.println("Error: question number " + question.questionNumber + " already exists");
			} else {
				bank.put(question.questionNumber, question);
				out.println("Question " + question.questionNumber + " added");
			}
			storeBank();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storeBank() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("bank.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(bank);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private int getPortNum() throws IOException {
		ServerSocket sock = new ServerSocket(0);
		int port = sock.getLocalPort();
		sock.close();
		return port;
	}


	
	private void updateMap() {
		FileInputStream fis;
		try {
			fis = new FileInputStream("bank.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			bank = (Map<Integer, Question>) ois.readObject();
			if(bank == null) bank = new HashMap<>();
			fis.close();
			ois.close();
		} catch (FileNotFoundException e) {
	
			bank = new HashMap<>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}