package motf; 
import motf.*;

@SuppressWarnings("unused")
public class PatternSizeComparator extends PatternComparator {
	public int compare(Pattern pat1, Pattern pat2) {
		int size1 = pat1.size();
		int size2 = pat2.size();
		return size1 - size2; 
	}
}

