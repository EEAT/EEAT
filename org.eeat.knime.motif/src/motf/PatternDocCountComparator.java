package motf; 
import motf.*;

@SuppressWarnings("unused")
public class PatternDocCountComparator extends PatternComparator {
	public int compare(Pattern pat1, Pattern pat2) {
		int size1 = pat1.getDocCount();
		int size2 = pat2.getDocCount();
		return size1 - size2; 
	}
}

