import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ContestHandler extends Thread {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private Scanner in;
	private ObjectInputStream obj;
	Contest contest;
	Set<String> nameSpace = new HashSet<>();
	boolean ready = false;
	Object lock = new Object();
	int index = 0;
	int numOfPlayer = 0;
	int tempNum = 0;
	int sum = 0;
	int correct = 0;
	int uncorrect = 0;
	int flagCount = 0;

	public ContestHandler(int port, Contest contest) {
		try {
			serverSocket = new ServerSocket(port);
			this.contest = contest;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllContestant() {
		try {
			
			serverSocket.setSoTimeout(20000);
			while(true) {
				clientSocket = serverSocket.accept(); 
				numOfPlayer++;
				new ContestantHandler(clientSocket, this).start();
			}
		} catch (SocketTimeoutException e1) {

		} catch (Exception e) {
			e.printStackTrace();
		}
		ready = true;
		synchronized(lock) {
			lock.notifyAll();
		}
	}

	public synchronized boolean checkName(String name) {
		if(nameSpace.contains(name)) {
			return false;
		} else {
			nameSpace.add(name);
			return true;
		}
	}
	
	public synchronized void incrementCorrect() {
		correct++;
		sum++;
	}

	//	public synchronized void incrementPlayerNum() {
	//		numOfPlayer++; 
	//	}
	public synchronized void flagIncrement() {
		flagCount++;
		if(flagCount == numOfPlayer) {
			ready = false;
		}
	}

	public synchronized void increment() {
		tempNum++;
		System.out.println("increment called " + tempNum + " tempNum, " + numOfPlayer + "players");
		if(tempNum == numOfPlayer) {
			synchronized(lock) {
				ready = true;
				contest.questionHist.put(contest.question.get(index), correct * 100/numOfPlayer);
				index++;
				tempNum = 0;
				System.out.println("notifyall");
				lock.notifyAll();
			}
			
			if(index == contest.question.size()) { //calculate average correct
				contest.averageCorrect = ((double)sum)/(numOfPlayer * contest.question.size());
				contest.status = true; 
			}
		}
	}

	public void run() {
		try {
			contest.status = true;
			getAllContestant();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] handleRes(String ans) {
		return null;
	}
}

class ContestantHandler extends Thread {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private Scanner in;
	private ObjectInputStream obj;
	ContestHandler callBack;
	ObjectOutputStream objOut;
	int score = 0;
	String start ;
	int index = 1;

	public ContestantHandler(Socket clientSocket,ContestHandler contest) {
		this.clientSocket = clientSocket;
		callBack = contest; 
	}

	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
			obj = new ObjectInputStream(clientSocket.getInputStream());
			objOut = new ObjectOutputStream(clientSocket.getOutputStream());

			String name = in.next();
			out.println("what ever dude");
			while(!callBack.checkName(name)) {
				out.println("Wrong");
				name = in.next();
				System.out.println(name);
			}
			out.println("ok");
			out.println(callBack.contest.question.size());
			waitFunction();
			while(callBack.index != callBack.contest.question.size()) {
				if(callBack.ready) {
					flipFlag();
				}
				Question q = callBack.contest.question.get(callBack.index);
				handleQuestion(q);
				waitFunction();
				printResponse();
				Thread.sleep(552);
			}
			
			out.println("the contest end, thanks for playing");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void printResponse() {
		out.println(start + ", " + (callBack.correct * 100)/callBack.numOfPlayer + "% of contestants answered this question correctly. ");
		out.println("your score is: " + score + "/" + callBack.contest.question.size() + ". The top score is currently " + callBack.contest.maxScore + "/" +  callBack.contest.question.size());
	}

	public void handleQuestion(Question q) {
		printQuestion(q);
		System.out.println("wait for ans");
		String ans = in.next();
		System.out.println("ans is: " + ans);
		if(ans.compareTo(q.correctRes) == 0) {
			score++;
			start = "Correct";
			callBack.contest.updateMaxScore(score);
			callBack.incrementCorrect(); 
		} else {
			start = "Incorrect";
		}
		callBack.contest.addResponse(ans == q.correctRes);
		callBack.increment();
	}

	public void flipFlag() {
		if(callBack.ready) {
			synchronized(callBack.lock) {
				callBack.correct = 0;
				callBack.ready = false;
			}
		}
	}

	public void waitFunction() {
		synchronized(callBack.lock) {
			while(!callBack.ready) {
				try {
					callBack.lock.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		callBack.flagIncrement();
	}

	private void printQuestion(Question question) {
		StringBuilder sb = new StringBuilder();
		sb.append("Question " + index);
		index++;
		sb.append("\n");
		sb.append(question.questionText + "\n");
		List<String> choices = question.choice;
		for(String s : choices) {
			sb.append(s + "\n");
		}
		out.println(sb.toString());
	}
}


