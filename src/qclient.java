import java.io.*;
import java.net.*;
import java.util.*;

public class qclient {
	static Socket client;
	public static void main(String[] args) {
		//GET HOSTNAME AND PORT FROM ARGS
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		try {
			client = new Socket("localhost", 54390);
	        DataInputStream reader = new DataInputStream(client.getInputStream());
	        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
	       
	        Scanner s=new Scanner(System.in);
			String line = "";
			int choice= 0;
			while (choice!= (int)'q' && choice!= (int)'k'){
				System.out.print("> ");
				line = s.nextLine();
				if (line.isEmpty()) {
            		System.out.println("Not an accepted command");
        		} 
        		else {
					choice = (int) line.charAt(0);
					
					if (choice == (int)'p'){ //put
						put(line, choice, writer, reader);
					}
					else if (choice == (int)'g'){
						get(line, choice, writer, reader);
					}
					else if (choice == (int)'d'){
						delete(line, choice, writer, reader);
					}
					
					else if (choice == (int)'c'){
						check(line, choice, writer, reader);
					}
					
					else if (choice == (int)'r'){
						random(line, choice, writer, reader);
					
					}
					else if(choice == (int)'q'){ //terminate client
						//quit(choice, writer);
						//writer.writeInt(choice);
						client.close();
					}
					else if(choice == (int)'k'){ //kill server
						writer.writeInt(choice);
						client.close();
					}
					else if(choice == (int)'h'){
						System.out.println("Use the following commands: ");
						System.out.println("p: put a question in the bank");
						System.out.println("d: delete question from the bank");
						System.out.println("g: get question from the bank");
						System.out.println("r: get random question from the bank and guess answer");
						System.out.println("c: check answer for a question");
						System.out.println("k: terminate the server");
						System.out.println("q: terminate the client");
						System.out.println("h: help");
					}
					else {
						System.out.println("Not an accepted command");
					}
				}
			}
	        
		}
		catch (IOException IOex){
            System.out.println("Server Error");
        }
		
		
	}
	
	public static void random(String line, int choice, DataOutputStream writer, DataInputStream reader){
		try {
			
			Scanner s=new Scanner(System.in);
	        writer.writeInt(choice); //tells server choice
	        
	        String output= reader.readUTF(); //printing question
	        System.out.print(output);
	        
	        String guess = s.next(); //gets guess
	        writer.writeUTF(guess); //tells server guess
	        
	        String output2 = reader.readUTF();
	        System.out.print(output2);
	        
	        
		}
		catch (IOException IOex)
        {
            System.out.println("Server Error");
        }
	}
	
	public static void check(String line, int choice, DataOutputStream writer, DataInputStream reader){
		try {
			
	        writer.writeInt(choice); //tells server choice
	        String[] splitStr = line.trim().split("\\s+");
	        int quesNum = Integer.parseInt(splitStr[1]);
	        String guess = splitStr[2];
	        //int quesNum = Integer.parseInt(line.substring(2));
	        writer.writeInt(quesNum);
	        writer.writeUTF(guess);
	        
	        
	       String output= reader.readUTF();
	       System.out.print(output);
	        
	        
		}
		catch (IOException IOex)
        {
            System.out.println("Server Error");
        }
	}
	public static void delete(String line, int choice, DataOutputStream writer, DataInputStream reader){
		try {
			
	        
	        writer.writeInt(choice); //tells server choice
	        String[] splitStr = line.trim().split("\\s+");
	        int quesNum = Integer.parseInt(splitStr[1]);
	        //int quesNum = Integer.parseInt(line.substring(2));
	        writer.writeInt(quesNum);
	        
	       String output= reader.readUTF();
	       System.out.print(output);
	        
	        
		}
		catch (IOException IOex)
        {
            System.out.println("Server Error");
        }
		
	}
	
	public static void get(String line, int choice, DataOutputStream writer, DataInputStream reader){
		try {
			
			Scanner s=new Scanner(System.in);
			
	        writer.writeInt(choice); //tells server choice
	        int quesNum = Integer.parseInt(line.substring(2));
	        //int quesNum = Integer.parseInt(s.next());
	        writer.writeInt(quesNum);
	        
	        String result = reader.readUTF();
	        System.out.print(result);
		}
		catch (IOException IOex)
        {
            System.out.println("Server Error");
        }
	}
	
	
	public static void put(String line, int choice, DataOutputStream writer, DataInputStream reader ){
		try {
		
	        
	        writer.writeInt(choice); //tells server choice
	        Scanner s=new Scanner(System.in);
	        
	        String tag = s.nextLine();
	        writer.writeUTF(tag);
	        
	        String question = s.nextLine();
	        String next1 = "";
	        while (!next1.equals(".")){
	        	next1 = s.nextLine();
	        	if (!next1.equals(".")){
	        		question +=  "\n" + next1;
	        	}
	        	
	        }
	        writer.writeUTF(question);
	        
	        //System.out.println("Question to put in:" + question);
	       // writer.writeUTF(question);
	        String next2 = s.nextLine();
	        int end = 0;
	        String questionString = next2 + "\n";
	        String withPeriods = ".\n" + next2 + "\n";
	        while (end!=1){
	        	String next = s.nextLine();
	        	String nextNext = s.nextLine();
	        	withPeriods += next + "\n"+ nextNext + "\n";
	        	if (next.equals(".")){
	        		if(nextNext.equals(".")){
	        			end = 1;
	        		}
	        		else{
	        			questionString += nextNext + "\n";
	        		}
	        	}
	        }
	        //System.out.print ("Choices with periods: " + withPeriods);
	        //System.out.println("Questions string: " + questionString);
	        String correctAns = s.nextLine();
	        writer.writeUTF(withPeriods);
	        writer.writeUTF(questionString);
	        writer.writeUTF(correctAns);
	        	
	        
	        int id = reader.readInt();
	        System.out.println(id);
		}
		catch (IOException IOex){
            System.out.println("Server Error");
        }
	}
}
