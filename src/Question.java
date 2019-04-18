import java.util.*;
import java.io.Serializable;  

public class Question implements Serializable{
	String tag;
	String questionText;
	String correctRes;
	int index;
	List<String> choice;
	int questionNumber;
	
	public Question() {
		choice = new ArrayList<>();
	}
}
