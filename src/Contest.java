import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Contest implements Serializable{
	List<Question> question;
	Map<Question, Integer> questionHist = new HashMap<>();
	boolean status ;
	double averageCorrect = 0;
	int maxScore = 0;
	int correctNum = 0;
	int uncorrectNum = 0;
	
	public Contest() {
		question = new ArrayList<>();
		status = false;
	}	
	
	
	public synchronized void updateMaxScore(int x) {
		maxScore = Math.max(x, maxScore);
	}
	
	public synchronized void addResponse(boolean res) {
		if(res) {
			correctNum++;
		} else {
			uncorrectNum++;
		}
	}
}
